package ro.chiralinteriordesign.cull.ui.auth

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import ro.chiralinteriordesign.cull.databinding.AuthLoginFragmentBinding
import ro.chiralinteriordesign.cull.ui.auth.forgot_password.ForgotPasswordFragment
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.pushFragment
import ro.chiralinteriordesign.cull.utils.showKeyboard

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: AuthViewModel by activityViewModels()
    private var binding: AuthLoginFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthLoginFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        setupAuthEdit(binding.emailInput, binding.emailTitle, viewModel.email)
        setupAuthEdit(binding.passwordInput, binding.passwordTitle, viewModel.password)

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.pushFragment(RegisterFragment.newInstance())
        }

        binding.btnContinue.setOnClickListener {
            val email = viewModel.email.value ?: return@setOnClickListener
            val password = viewModel.password.value ?: return@setOnClickListener

            viewModel.login(email, password).observe(viewLifecycleOwner) {
                if (it == null) {
                    requireActivity().finish()
                } else {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            parentFragmentManager.pushFragment(ForgotPasswordFragment.newInstance())
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}