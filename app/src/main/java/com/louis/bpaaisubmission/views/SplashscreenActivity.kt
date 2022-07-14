package com.louis.bpaaisubmission.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.louis.bpaaisubmission.R
import com.louis.bpaaisubmission.utils.ViewModelFactory
import com.louis.bpaaisubmission.viewmodels.MainViewModel
import kotlinx.coroutines.*

class SplashscreenActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        scope.launch {
            delay(timeDelay)
            setupLogin()
        }
    }

    private fun setupLogin() {
        mainViewModel.getSession().observe(this@SplashscreenActivity) { session ->
            if(session.isLogin) {
                Intent(this@SplashscreenActivity, MainActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Intent(this@SplashscreenActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }

            finish()
        }
    }

    override fun onPause() {
        scope.cancel()
        super.onPause()
    }

    companion object {
        const val timeDelay = 3000L
    }
}