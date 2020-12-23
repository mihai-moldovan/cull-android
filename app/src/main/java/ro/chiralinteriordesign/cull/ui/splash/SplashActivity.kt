package ro.chiralinteriordesign.cull.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Constants
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ActivitySplashBinding
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity
import ro.chiralinteriordesign.cull.ui.tutorial.TutorialActivity


class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        viewModel.ensureAllLoaded().observe(this, Observer {
            if (it) {
                GlobalScope.launch {
                    delay(300)
                    advance()
                }
            } else {
                Snackbar.make(binding.root, R.string.generic_error, Snackbar.LENGTH_INDEFINITE)
                    .show()
                GlobalScope.launch {
                    delay(2000)
                    finish()
                }
            }
        })
    }

    private fun advance() {
        finish()
        if (System.currentTimeMillis() - App.instance.preferences.getLong(
                Preferences.Key.TUTORIAL_SEEN, 0
            ) > Constants.TUTORIAL_INTERVAL
        ) {
            startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
        }
    }
}