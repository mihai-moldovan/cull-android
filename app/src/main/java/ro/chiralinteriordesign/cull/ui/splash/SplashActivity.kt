package ro.chiralinteriordesign.cull.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Constants
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.SplashActivityBinding
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.auth.reset_password.ResetPasswordActivity
import ro.chiralinteriordesign.cull.ui.products.ProductsActivity
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity
import ro.chiralinteriordesign.cull.ui.space.SelectSpaceActivity
import ro.chiralinteriordesign.cull.ui.tutorial.TutorialActivity


class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.show()

        viewModel.ensureAllLoaded().observe(this, Observer {
            binding.progressBar.hide()
            if (it) {
                GlobalScope.launch {
                    delay(200)
                    checkDynamicLink()
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


    private fun checkDynamicLink() {
        val uri = intent.data
        intent.data = null
        if (uri != null && intent.action == Intent.ACTION_VIEW) {
            if (uri.path?.startsWith("/password_reset") == true) {
                uri.getQueryParameter("token")?.let {
                    startActivity(ResetPasswordActivity.getIntent(this, it))
                    finish()
                    return
                }
            }
        }
        advance()
    }

    private fun advance() {
        val user = App.instance.dataRepository.userRepository.currentUser
        when {
            System.currentTimeMillis() - App.instance.preferences.getLong(
                Preferences.Key.TUTORIAL_SEEN,
                0
            ) > Constants.TUTORIAL_INTERVAL -> {
                //didn't show tutorial
                startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
            }
            user.quizResult.isEmpty() -> {
                //has no quiz done
                startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
            }
            user.rooms.isNullOrEmpty() -> {
                //has has no space saved
                startActivity(Intent(this@SplashActivity, SelectSpaceActivity::class.java))
            }
            else -> {
                //show products
                startActivity(Intent(this@SplashActivity, ProductsActivity::class.java))
            }
        }
        finish()
    }
}