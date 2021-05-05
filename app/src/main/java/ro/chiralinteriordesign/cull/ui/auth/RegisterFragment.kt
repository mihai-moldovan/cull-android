package ro.chiralinteriordesign.cull.ui.auth

import android.app.Activity
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
import ro.chiralinteriordesign.cull.databinding.AuthRegisterFragmentBinding
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.showKeyboard

/**
 * Created by Mihai Moldovan on 03/01/2021.
 */
class RegisterFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterFragment()
    }

    private val viewModel: AuthViewModel by activityViewModels()
    private var binding: AuthRegisterFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AuthRegisterFragmentBinding.inflate(inflater, container, false)
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

        setupAuthEdit(binding.lastNameInput, binding.lastNameTitle, viewModel.lastName)
        setupAuthEdit(binding.firstNameInput, binding.firstNameTitle, viewModel.firstName)
        setupAuthEdit(binding.emailInput, binding.emailTitle, viewModel.email)
        setupAuthEdit(binding.passwordInput, binding.passwordTitle, viewModel.password)
        setupAuthEdit(binding.password2Input, binding.password2Title, viewModel.password2)

        binding.termsCheckbox.setOnClickListener {
            viewModel.termsAgreed.postValue(!(viewModel.termsAgreed.value ?: false))
        }

        viewModel.termsAgreed.observe(viewLifecycleOwner) {
            binding.termsCheckbox.isChecked = it
        }

        binding.btnContinue.setOnClickListener {
            val firstName = viewModel.firstName.value ?: return@setOnClickListener
            val lastName = viewModel.lastName.value ?: return@setOnClickListener
            val email = viewModel.email.value ?: return@setOnClickListener
            val password = viewModel.password.value ?: return@setOnClickListener
            val password2 = viewModel.password2.value ?: return@setOnClickListener

            viewModel.register(firstName, lastName, email, password).observe(viewLifecycleOwner) {
                if (it == null) {
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()
                } else {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}