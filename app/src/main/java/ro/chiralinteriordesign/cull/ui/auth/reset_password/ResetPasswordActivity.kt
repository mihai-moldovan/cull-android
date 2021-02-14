package ro.chiralinteriordesign.cull.ui.auth.reset_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import ro.chiralinteriordesign.cull.databinding.AuthResetPasswordActivityBinding
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.auth.setupAuthEdit

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
        binding = AuthResetPasswordActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupAuthEdit(binding.passwordInput, binding.passwordTitle, viewModel.password)
        setupAuthEdit(binding.passwordInput2, binding.passwordTitle2, viewModel.password2)

        binding.btnContinue.setOnClickListener {
            viewModel.resetPassword().observe(this) {
                finish()
            }
        }

        intent.getStringExtra(ARG_TOKEN)?.let {
            token = it
        } ?: run {
            finish()
        }
    }
}