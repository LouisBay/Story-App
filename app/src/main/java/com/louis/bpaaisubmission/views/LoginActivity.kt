package com.louis.bpaaisubmission.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.data.remote.model.SessionModel
import com.louis.bpaaisubmission.data.remote.response.LoginResult
import com.louis.bpaaisubmission.databinding.ActivityLoginBinding
import com.louis.bpaaisubmission.utils.Result
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimate()

        binding.apply {
            btnLogin.setOnClickListener(this@LoginActivity)
            tvRegister.setOnClickListener(this@LoginActivity)
        }
    }

    private fun playAnimate() {
        with(binding) {
            ObjectAnimator.ofFloat(ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val welcome = ObjectAnimator.ofFloat(tvWelcome, View.ALPHA, 1f).setDuration(200)
            val welcomeMessage = ObjectAnimator.ofFloat(tvWelcomeMessage, View.ALPHA, 1f).setDuration(200)
            val email = ObjectAnimator.ofFloat(tvEmail, View.ALPHA, 1f).setDuration(200)
            val etlEmail = ObjectAnimator.ofFloat(etlEmail, View.ALPHA, 1f).setDuration(200)
            val password = ObjectAnimator.ofFloat(tvPassword, View.ALPHA, 1f).setDuration(200)
            val etlPassword = ObjectAnimator.ofFloat(etlPassword, View.ALPHA, 1f).setDuration(200)
            val btnLogin = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(200)
            val bottomText = ObjectAnimator.ofFloat(containerTextBottom, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(welcome, welcomeMessage, email, etlEmail, password, etlPassword, btnLogin, bottomText)
                startDelay = 300
                start()
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                closeKeyboard()
                startLogin()
            }

            R.id.tv_register -> {
                Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun startLogin() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        var isInputValid = true

        if (email.isBlank()){
            isInputValid = false
            binding.edtEmail.error = resources.getString(R.string.empty_field)
        }

        if (password.isBlank()){
            isInputValid = false
            binding.edtPassword.error = resources.getString(R.string.empty_field)
        }

        if(isInputValid) {
            loginViewModel
                .loginUser(email, password)
                .observe(this@LoginActivity) { result ->
                    processResult(result)
                }
        }
    }

    private fun processResult(result: Result<LoginResult?>?) {
        when (result) {
            is Result.Loading -> { showLoading(true) }

            is Result.Success -> {
                val data = result.data

                loginViewModel.saveSession(SessionModel(true, data?.token.toString()))

                Intent(this@LoginActivity, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }

                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is Result.Error -> {
                showLoading(false)
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.login_failure),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    private fun showLoading(state: Boolean) {
        binding.apply {
            if(state) {
                containerLoading.viewLoading.visibility = View.VISIBLE
                etlEmail.visibility = View.GONE
                etlPassword.visibility = View.GONE
                btnLogin.visibility = View.GONE
                containerTextBottom.visibility = View.GONE
            }
            else {
                containerLoading.viewLoading.visibility = View.GONE
                etlEmail.visibility = View.VISIBLE
                etlPassword.visibility = View.VISIBLE
                btnLogin.visibility = View.VISIBLE
                containerTextBottom.visibility = View.VISIBLE
            }
        }
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}