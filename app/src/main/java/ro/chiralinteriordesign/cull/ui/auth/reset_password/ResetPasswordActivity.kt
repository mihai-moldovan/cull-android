package ro.chiralinteriordesign.cull.ui.auth.reset_password

import android.os.Bundle
import androidx.activity.viewModels
import ro.chiralinteriordesign.cull.databinding.AuthResetPasswordActivityBinding
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.auth.setupAuthEdit

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
class ResetPasswordActivity : BaseActivity() {

    private lateinit var binding: AuthResetPasswordActivityBinding
    private val viewModel: ResetPasswordViewModel by viewModels()

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
    }
}