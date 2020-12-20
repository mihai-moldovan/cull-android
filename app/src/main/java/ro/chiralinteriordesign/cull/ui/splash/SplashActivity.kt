package ro.chiralinteriordesign.cull.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Constants
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity
import ro.chiralinteriordesign.cull.ui.tutorial.TutorialActivity

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(1000)
            this@SplashActivity.finish()
            advance()
        }
    }

    private fun advance() {
        if (System.currentTimeMillis() - App.instance.preferences.getLong(
                Preferences.Key.TUTORIAL_SEEN,
                0
            ) > Constants.TUTORIAL_INTERVAL
        ) {
            startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
        }
    }
}