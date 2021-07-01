package ro.chiralinteriordesign.cull.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.CartItemBinding
import ro.chiralinteriordesign.cull.databinding.CartItemTotalBinding
import ro.chiralinteriordesign.cull.databinding.FragmentCartBinding
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.CartLineItem
import ro.chiralinteriordesign.cull.ui.auth.AuthActivity
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
    private val adapter: Adapter = Adapter(object : AdapterListener {
        override fun onItemClick(item: CartLineItem, pos: Int) {
            parentFragmentManager.pushFragment(ProductDetailsFragment.newInstance(item.product, viewModel.cartIndex))
        }

        override fun onQuantityDecrease(item: CartLineItem, post: Int) {
            viewModel.removeProduct(item.product, false)
        }

        override fun onQuantityIncrease(item: CartLineItem, pos: Int) {
            viewModel.addProduct(item.product)
        }

        override fun onRemoveItem(item: CartLineItem, pos: Int) {
            viewModel.removeProduct(item.product, true)
        }
    })

    private val requestOfferContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            requestOffer()
        }
    }


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
        viewModel.cartIndex = cartIndex
        viewModel.cart.value = cart
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
        binding.navBar.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.recyclerView.adapter = adapter
        binding.btnContinue.setOnClickListener {
            val cart = viewModel.cart.value ?: return@setOnClickListener
            if (cart.lineItems.isEmpty()) {
                return@setOnClickListener
            }
            if (viewModel.isLoggedIn) {
                requestOffer()
            } else {
                requestOfferContent.launch(Intent(requireActivity(), AuthActivity::class.java))
            }
        }

        viewModel.cart.observe(viewLifecycleOwner) {
            adapter.cart = it
            binding.navBar.titleView.text = it.name
            if (it.isSent || it.lineItems.isEmpty()) {
                binding.btnContinue.visibility = View.INVISIBLE
            } else {
                binding.btnContinue.visibility = View.VISIBLE
            }
            binding.offerSentText.visibility = if (it.isSent) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun requestOffer() {
        val binding = binding ?: return
        val cart = viewModel.cart.value ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.btnContinue.visibility = View.INVISIBLE
        viewModel.requestOffer().observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.INVISIBLE
            if (!it) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.offer_failed)
                    .setPositiveButton(R.string.alert_ok, null)
                    .show()
                binding.btnContinue.visibility = View.VISIBLE
            }
        }
    }

    class TotalItemViewHolder(val binding: CartItemTotalBinding) : RecyclerView.ViewHolder(binding.root) {
        var priceValue: String? = null
            set(newValue) {
                field = newValue
                binding.priceView.text = newValue
            }
    }

    class LineItemViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {

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
                    binding.priceView.text = it.priceString
                    binding.quantityView.text = it.quantity.toString()
                }
            }

        var archived: Boolean = false
            set(newValue) {
                field = newValue
                val visibility = if (newValue) View.INVISIBLE else View.VISIBLE
                binding.buttonDecrease.visibility = visibility
                binding.buttonIncrease.visibility = visibility
                binding.buttonDelete.visibility = visibility
            }
    }

    interface AdapterListener {
        fun onItemClick(item: CartLineItem, pos: Int)
        fun onQuantityIncrease(item: CartLineItem, pos: Int)
        fun onQuantityDecrease(item: CartLineItem, post: Int)
        fun onRemoveItem(item: CartLineItem, pos: Int)
    }

    class Adapter(private val listener: AdapterListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            const val VIEW_PRODUCT = 0
            const val VIEW_TOTAL = 1
        }

        var cart: Cart? = null
            set(newValue) {
                field = newValue
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            val size = cart?.lineItems?.size ?: 0
            return if (size > 0) size + 1 else 0
        }

        override fun getItemViewType(position: Int): Int = if (position < cart?.lineItems?.size ?: 0) VIEW_PRODUCT else VIEW_TOTAL


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_PRODUCT -> LineItemViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                    .apply {
                        itemView.setOnClickListener {
                            lineItem?.let { lineItem ->
                                listener.onItemClick(lineItem, adapterPosition)
                            }
                        }
                        binding.buttonIncrease.setOnClickListener {
                            lineItem?.let { lineItem ->
                                listener.onQuantityIncrease(lineItem, adapterPosition)
                            }
                        }
                        binding.buttonDecrease.setOnClickListener {
                            lineItem?.let { lineItem ->
                                listener.onQuantityDecrease(lineItem, adapterPosition)
                            }
                        }
                        binding.buttonDelete.setOnClickListener {
                            lineItem?.let {
                                listener.onRemoveItem(it, adapterPosition)
                            }
                        }
                    }
                VIEW_TOTAL -> TotalItemViewHolder(CartItemTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                else -> throw NotImplementedError("not implemented")
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is LineItemViewHolder -> cart?.let {
                    holder.lineItem = it.lineItems[position]
                    holder.archived = it.isSent
                }
                is TotalItemViewHolder -> holder.priceValue = cart?.totalString
            }
        }
    }
}

