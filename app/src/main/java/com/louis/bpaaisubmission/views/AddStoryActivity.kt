package com.louis.bpaaisubmission.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.databinding.ActivityAddStoryBinding
import com.louis.bpaaisubmission.utils.Helper.toBearerToken
import com.louis.bpaaisubmission.utils.MediaUtils.createCustomTempFile
import com.louis.bpaaisubmission.utils.MediaUtils.reduceFileImage
import com.louis.bpaaisubmission.utils.MediaUtils.rotateBitmap
import com.louis.bpaaisubmission.utils.MediaUtils.uriToFile
import com.louis.bpaaisubmission.utils.Result
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.AddStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null
    private var token: String = ""
    private var location: Location? = null

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val resultBitmap = rotateBitmap(BitmapFactory.decodeFile(myFile.path), currentPhotoPath)

            try {
                FileOutputStream(myFile).apply {
                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
                    flush()
                    close()
                }
            } catch (e: Exception) { e.printStackTrace() }

            getFile = myFile

            binding.ivStory.setImageBitmap(resultBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.ivStory.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        getFile = null
        location = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        getSessionToken()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener {
                closeKeyboard()
                uploadImage()
            }

            switchShareLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) getMyLocation()
                else location = null
            }
        }
    }

    private fun getSessionToken() {
        addStoryViewModel.getSession().observe(this) {
            token = it.token
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this,
                "com.louis.bpaaisubmission",
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
            val chooser = Intent.createChooser(this, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }
    }

    private fun uploadImage(){
        var isInputValid = true

        if(getFile == null) {
            isInputValid = false
            Snackbar.make(
                binding.root,
                resources.getString(R.string.please_select_image),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        if (binding.edtDescription.text.toString().isBlank()) {
            isInputValid = false
            Snackbar.make(
                binding.root,
                resources.getString(R.string.please_fill_description),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        if(isInputValid) {
            val file = reduceFileImage(getFile as File)

            val description = binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (location != null) {
                location?.apply {
                    lat = latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = longitude.toString().toRequestBody("text/plain".toMediaType())
                }
            }

            addStoryViewModel.apply {
                uploadNewStory(token.toBearerToken(), description, lat, lon, imageMultipart)
                result.observe(this@AddStoryActivity) { result ->
                    when(result) {
                        is Result.Loading -> { showLoading(true) }
                        is Result.Success -> {
                            finish()
                            Toast.makeText(
                                applicationContext,
                                resources.getString(R.string.new_story_uploaded),
                                Toast.LENGTH_SHORT
                            ).show()

                            showLoading(false)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Snackbar.make(
                                binding.root,
                                resources.getString(R.string.error, result.errorMessage),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.apply {
            if(state) {
                viewLoading.visibility = View.VISIBLE
                btnCamera.visibility = View.GONE
                btnGallery.visibility = View.GONE
                btnUpload.visibility = View.GONE
                switchShareLocation.visibility = View.GONE
                etlDescription.visibility = View.GONE
            }
            else {
                viewLoading.visibility = View.GONE
                btnCamera.visibility = View.VISIBLE
                btnGallery.visibility = View.VISIBLE
                btnUpload.visibility = View.VISIBLE
                switchShareLocation.visibility = View.VISIBLE
                etlDescription.visibility = View.VISIBLE
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> { getMyLocation() }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> { getMyLocation() }
                else -> {
                    Toast.makeText(applicationContext, resources.getString(R.string.please_grant_permission), Toast.LENGTH_SHORT).show()
                    binding.switchShareLocation.isChecked = false
                }
            }
        }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    this.location = loc
                } else {
                    Toast.makeText(applicationContext, resources.getString(R.string.please_active_location_service), Toast.LENGTH_SHORT).show()
                    binding.switchShareLocation.isChecked = false
                }

            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}