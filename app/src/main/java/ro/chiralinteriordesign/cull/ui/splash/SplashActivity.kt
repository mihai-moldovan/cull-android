package ro.chiralinteriordesign.cull.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.MainActivity
import ro.chiralinteriordesign.cull.R

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(3000)
            this@SplashActivity.finish()
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }
}