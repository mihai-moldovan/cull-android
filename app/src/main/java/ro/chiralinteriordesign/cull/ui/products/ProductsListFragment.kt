package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ro.chiralinteriordesign.cull.databinding.ProductsListFragmentBinding
import ro.chiralinteriordesign.cull.model.text.Text
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
        binding?.buttonMenu?.setOnClickListener {
//            startActivity(
//                TextsActivity.getIntent(
//                    requireContext(),
//                    "TERMENI TEST CONDITII",
//                    textKey = Text.Key.TERMS,
//                )
//            )
            val fr = MenuFragment.newInstance()
            fr.showNow(parentFragmentManager, MenuFragment.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}