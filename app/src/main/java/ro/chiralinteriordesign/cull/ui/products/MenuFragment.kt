package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.MenuFragmentBinding

/**
 * Created by Mihai Moldovan on 01/01/2021.
 */
class MenuFragment : DialogFragment() {

    companion object {

        const val TAG = "Menu"
        fun newInstance(): MenuFragment = MenuFragment()
    }

    private var binding: MenuFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_CULL)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MenuFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}