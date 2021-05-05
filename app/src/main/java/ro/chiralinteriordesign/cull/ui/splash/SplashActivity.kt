package ro.chiralinteriordesign.cull.ui.splash

import android.app.Activity
import android.content.Context
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

    companion object {
        fun showInitialActivity(context: Context) {
            val user = App.instance.dataRepository.userRepository.currentUser
            val room = App.instance.dataRepository.userRepository.room
            when {
                context !is TutorialActivity && System.currentTimeMillis() - App.instance.preferences.getLong(
                    Preferences.Key.TUTORIAL_SEEN, 0
                ) > Constants.TUTORIAL_INTERVAL -> {
                    //didn't show tutorial
                    context.startActivity(Intent(context, TutorialActivity::class.java))
                }
                context !is QuizActivity && user.quizResult.isNullOrBlank() -> {
                    //has no quiz done
                    context.startActivity(Intent(context, QuizActivity::class.java))
                }
                context !is SelectSpaceActivity && room == null -> {
                    //has has no space saved
                    context.startActivity(Intent(context, SelectSpaceActivity::class.java))
                }
                else -> {
                    //show products
                    context.startActivity(Intent(context, ProductsActivity::class.java))
                }
            }
            (context as? Activity)?.finish()
        }
    }

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
        showInitialActivity(this)
    }
}