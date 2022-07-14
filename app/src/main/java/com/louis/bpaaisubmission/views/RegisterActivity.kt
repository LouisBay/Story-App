package com.louis.bpaaisubmission.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.remote.response.RegisterResponse
import com.louis.bpaaisubmission.databinding.ActivityRegisterBinding
import com.louis.bpaaisubmission.utils.Result
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.RegisterViewModel

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimate()

        binding.apply {
            btnRegister.setOnClickListener(this@RegisterActivity)
            tvLogin.setOnClickListener(this@RegisterActivity)
        }
    }

    private fun showLoading(state: Boolean) {
        binding.containerLoading.apply {
            if(state) viewLoading.visibility = View.VISIBLE
            else viewLoading.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_register -> { startRegister() }

            R.id.tv_login -> {
                finish()
            }
        }
    }

    private fun processResult(result: Result<RegisterResponse?>?) {

        when (result) {
            is Result.Loading -> { showLoading(true) }

            is Result.Success -> {
                finish()

                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is Result.Error -> {
                showLoading(false)
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.register_failure),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    private fun startRegister() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        var isInputValid = true

        if (name.isBlank()){
            isInputValid = false
            binding.edtName.error = resources.getString(R.string.empty_field)
        }

        if (email.isBlank()){
            isInputValid = false
            binding.edtEmail.error = resources.getString(R.string.empty_field)
        }

        if (password.isBlank()){
            isInputValid = false
            binding.edtPassword.error = resources.getString(R.string.empty_field)
        }

        if(isInputValid) {
            registerViewModel
                .registerUser(name, email, password)
                .observe(this@RegisterActivity) {
                    processResult(it)
                }
        }
    }

    private fun playAnimate() {
        with(binding) {
            ObjectAnimator.ofFloat(ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val register = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(200)
            val registerMessage = ObjectAnimator.ofFloat(tvRegisterMessage, View.ALPHA, 1f).setDuration(200)
            val email = ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 1f).setDuration(200)
            val etlEmail = ObjectAnimator.ofFloat(etlEmail, View.ALPHA, 1f).setDuration(200)
            val name = ObjectAnimator.ofFloat(tvName, View.ALPHA, 1f).setDuration(200)
            val etlName = ObjectAnimator.ofFloat(etlName, View.ALPHA, 1f).setDuration(200)
            val password = ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 1f).setDuration(200)
            val etlPassword = ObjectAnimator.ofFloat(etlPassword, View.ALPHA, 1f).setDuration(200)
            val btnRegister = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(200)
            val bottomText = ObjectAnimator.ofFloat(containerTextBottom, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(register, registerMessage, name, etlName, email, etlEmail, password, etlPassword, btnRegister, bottomText)
                startDelay = 300
                start()
            }
        }
    }
}