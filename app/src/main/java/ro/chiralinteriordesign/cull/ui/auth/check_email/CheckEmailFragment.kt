package ro.chiralinteriordesign.cull.ui.auth.check_email

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.AuthCheckEmailFragmentBinding


/**
 * Created by Mihai Moldovan on 12/01/2021.
 */

private const val ARG_TYPE = "type"

class CheckEmailFragment : Fragment() {

    enum class Type {
        ACTIVATE_ACCOUNT, RESET_PASSWORD
    }

    companion object {
        fun newInstance(type: Type) = CheckEmailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TYPE, type)
            }
        }
    }

    private lateinit var type: Type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getSerializable(ARG_TYPE) as Type
        }
    }

    private var binding: AuthCheckEmailFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthCheckEmailFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        when (type) {
            Type.ACTIVATE_ACCOUNT -> {
                binding.titleView.setText(R.string.auth_activate_success)
                binding.messageView.setText(R.string.auth_activate_message)
                binding.btnContinue.setText(R.string.auth_activate_open_email)
            }
            Type.RESET_PASSWORD -> {
                binding.titleView.setText(R.string.auth_forgot_pass_in_progress)
                binding.messageView.setText(R.string.auth_forgot_pass_check_email)
                binding.btnContinue.setText(R.string.auth_activate_open_email)
            }
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent.makeMainSelectorActivity(
                Intent.ACTION_MAIN,
                Intent.CATEGORY_APP_EMAIL
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(Intent.createChooser(intent, "Email"))
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}