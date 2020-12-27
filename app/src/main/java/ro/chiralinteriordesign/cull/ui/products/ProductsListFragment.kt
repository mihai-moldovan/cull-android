package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ro.chiralinteriordesign.cull.databinding.ProductsListFragmentBinding
import ro.chiralinteriordesign.cull.ui.texts.TextsActivity

class ProductsListFragment : Fragment() {

    private var binding: ProductsListFragmentBinding? = null

    companion object {
        fun newInstance() = ProductsListFragment()
    }

    private val viewModel: ProductsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProductsListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.message?.setOnClickListener {
            startActivity(
                TextsActivity.getIntent(
                    requireContext(),
                    "TERMENI TEST CONDITII",
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}