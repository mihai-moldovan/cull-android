package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ro.chiralinteriordesign.cull.databinding.ProductsFiltersFragmentBinding

class ProductsFiltersFragment : Fragment() {

    private var binding: ProductsFiltersFragmentBinding? = null
    private val viewModel: ProductsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProductsFiltersFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


}