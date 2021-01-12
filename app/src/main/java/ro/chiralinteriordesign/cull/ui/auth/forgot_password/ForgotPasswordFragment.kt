package ro.chiralinteriordesign.cull.ui.auth.forgot_password

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.AuthForgotPasswordFragmentBinding
import ro.chiralinteriordesign.cull.ui.auth.check_email.CheckEmailFragment
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.showKeyboard

class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    private val viewModel: ForgotPasswordViewModel by viewModels()
    private var binding: AuthForgotPasswordFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AuthForgotPasswordFragmentBinding.inflate(inflater, container, false)
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

        setupEdit(binding.emailInput, binding.emailTitle, viewModel.email)

        binding.btnContinue.setOnClickListener {
            viewModel.sendEmail()?.observe(viewLifecycleOwner) {
                if (it == null) {
                    val fr = CheckEmailFragment.newInstance(CheckEmailFragment.Type.RESET_PASSWORD)
                    parentFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.push_enter,
                            R.anim.push_exit,
                            R.anim.pop_enter,
                            R.anim.pop_exit
                        )
                        .replace(R.id.childContainer, fr, tag ?: fr.javaClass.name)
                        .commit()
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

    private fun setupEdit(
        inputLayout: TextInputLayout,
        titleView: TextView,
        liveData: MutableLiveData<String>
    ) {
        val editText = inputLayout.editText ?: return
        inputLayout.setOnClickListener {
            editText.requestFocus()
        }
        titleView.setOnClickListener {
            editText.requestFocus()
        }
        editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                liveData.postValue(s?.toString())
            }
        })
        editText.setOnFocusChangeListener { _, hasFocus ->
            titleView.isSelected = hasFocus
            inputLayout.error = null
            if (hasFocus) {
                editText.showKeyboard()
            }
        }
    }


}