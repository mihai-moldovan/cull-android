package ro.chiralinteriordesign.cull.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.CartItemBinding
import ro.chiralinteriordesign.cull.databinding.FragmentCartBinding
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.CartLineItem
import ro.chiralinteriordesign.cull.ui.products.ProductDetailsFragment
import ro.chiralinteriordesign.cull.utils.pushFragment

private const val ARG_CART_INDEX = "cart_index"

class CartFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(cartIndex: Int) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CART_INDEX, cartIndex)
                }
            }
    }

    private val viewModel: CartViewModel by viewModels()
    private var binding: FragmentCartBinding? = null
    private lateinit var adapter: Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cartIndex = arguments?.getInt(ARG_CART_INDEX) ?: run {
            parentFragmentManager.popBackStack()
            return
        }
        val cart = App.instance.dataRepository.productRepository.carts?.get(cartIndex) ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        adapter = Adapter(cart) { lineItem, _ ->
            parentFragmentManager.pushFragment(ProductDetailsFragment.newInstance(lineItem.product, cartIndex))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.navBar.titleView.text = adapter.cart.name
        binding.navBar.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.recyclerView.adapter = adapter
        binding.btnContinue.setOnClickListener {
            val cart = adapter.cart
            if (cart.lineItems.isEmpty()) {
                return@setOnClickListener
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    class LineItemViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var lineItem: CartLineItem? = null
            set(newValue) {
                field = newValue
                newValue?.let {
                    Glide.with(binding.imageView)
                        .load(it.product.images.firstOrNull())
                        .placeholder(R.drawable.product_placeholder)
                        .into(binding.imageView)
                    binding.titleView.text = it.product.title
                    binding.subtitleView.text = it.product.category
                    binding.priceView.text = it.product.priceString
                    binding.quantityView.text = it.quantity.toString()
                }
            }
    }

    class Adapter(
        val cart: Cart,
        private val onItemClickListener: ((item: CartLineItem, pos: Int) -> Unit)
    ) : RecyclerView.Adapter<LineItemViewHolder>() {

        override fun getItemCount(): Int {
            return cart.lineItems.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
            return LineItemViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                .apply {
                    itemView.setOnClickListener {
                        lineItem?.let { lineItem ->
                            onItemClickListener(lineItem, adapterPosition)
                        }
                    }
                }
        }

        override fun onBindViewHolder(holder: LineItemViewHolder, position: Int) {
            holder.lineItem = cart.lineItems[position]
        }
    }
}

