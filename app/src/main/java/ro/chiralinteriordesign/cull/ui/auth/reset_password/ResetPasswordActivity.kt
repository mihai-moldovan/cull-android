package ro.chiralinteriordesign.cull.ui.auth.reset_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import ro.chiralinteriordesign.cull.databinding.AuthResetPasswordActivityBinding
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.auth.setupAuthEdit
import ro.chiralinteriordesign.cull.ui.splash.SplashActivity

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */

private const val ARG_TOKEN = "token"

class ResetPasswordActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun getIntent(context: Context, token: String) =
            Intent(context, ResetPasswordActivity::class.java).apply {
                putExtra(ARG_TOKEN, token)
            }
    }

    private lateinit var binding: AuthResetPasswordActivityBinding
    private val viewModel: ResetPasswordViewModel by viewModels()
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(ARG_TOKEN)?.let {
            token = it
        } ?: run {
            finish()
            return
        }

        binding = AuthResetPasswordActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupAuthEdit(binding.passwordInput, binding.passwordTitle, viewModel.password)
        setupAuthEdit(binding.passwordInput2, binding.passwordTitle2, viewModel.password2)

        binding.btnContinue.setOnClickListener {
            viewModel.resetPassword(token).observe(this) { error ->
                if (error != null) {
                    Snackbar.make(it, error, Snackbar.LENGTH_LONG).show()
                } else {
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                }
            }
        }


        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}