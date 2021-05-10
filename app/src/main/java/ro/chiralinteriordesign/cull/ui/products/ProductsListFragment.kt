package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ProductsItemLoadingBinding
import ro.chiralinteriordesign.cull.databinding.ProductsItemProductListBinding
import ro.chiralinteriordesign.cull.databinding.ProductsListFragmentBinding
import ro.chiralinteriordesign.cull.model.shop.Product
import ro.chiralinteriordesign.cull.ui.cart.CartFragment
import ro.chiralinteriordesign.cull.utils.hideKeyboard
import ro.chiralinteriordesign.cull.utils.pushFragment

private const val ARG_SEARCH_TERM = "arg_search_term"
private const val ARG_CART_INDEX = "cart_index"

class ProductsListFragment : Fragment() {

    companion object {
        fun newInstance(
            searchTerm: String? = null,
            cartIndex: Int? = null
        ) = ProductsListFragment().apply {
            arguments = Bundle().apply {
                searchTerm?.let { putString(ARG_SEARCH_TERM, it) }
                cartIndex?.let { putInt(ARG_CART_INDEX, it) }
            }
        }
    }


    private var binding: ProductsListFragmentBinding? = null
    private val adapter = ProductsAdapter(
        {
            parentFragmentManager.pushFragment(ProductDetailsFragment.newInstance(it, viewModel.currentCartIndex))
        },
        {
            viewModel.loadNextPage()
        })


    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_SEARCH_TERM)?.let {
            viewModel.searchQuery(it, arguments?.getInt(ARG_CART_INDEX, 0) ?: 0)
        } ?: run {
            viewModel.loadRoomAndStyle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProductsListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.buttonMenu.setOnClickListener {
            val fr = MenuFragment.newInstance()
            fr.showNow(parentFragmentManager, MenuFragment.TAG)
        }

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.query.observe(viewLifecycleOwner) {
            binding.navBar.titleView.text = it
        }

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            val binding = this.binding ?: return@Observer
            if (isLoading && adapter.data.isEmpty()) {
                binding.loadingView.visibility = View.VISIBLE
                binding.productView.visibility = View.GONE

                val startAni = object : Runnable {
                    override fun run() {
                        binding.progressBackground
                            .animate()
                            .rotationBy(45.0f)
                            .setDuration(125)
                            .withEndAction(this)
                            .setInterpolator(LinearInterpolator())
                            .start()
                    }
                }
                startAni.run()
            } else {
                binding.loadingView.visibility = View.GONE
                binding.productView.visibility = View.VISIBLE
                binding.progressBackground.clearAnimation()
            }
            adapter.isLoading = isLoading
            binding.refreshLayout.isRefreshing = isLoading
        })


        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null

        binding.refreshLayout.setOnRefreshListener {
            viewModel.loadRoomAndStyle()
            viewModel.loadNextPage()
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.data = it
        }

        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.hideKeyboard()
                val text = v.text.toString()
                if (text.isNotBlank()) {
                    val fr = newInstance(text, viewModel.currentCartIndex)
                    parentFragmentManager.pushFragment(fr)
                }
                true
            } else {
                false
            }
        }

        viewModel.roomStyle.observe(viewLifecycleOwner) {
            binding.roomTypeLabel.text = it
        }


        binding.buttonCart.setOnClickListener {
            viewModel.currentCartIndex?.let {
                parentFragmentManager.pushFragment(CartFragment.newInstance(it))
            }
        }

        binding.searchBar.visibility =
            if (viewModel.isSearchScreen) View.INVISIBLE else View.VISIBLE
        binding.buttonMenu.visibility = binding.searchBar.visibility
        binding.navBar.navBarContainer.visibility =
            if (binding.searchBar.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        binding.buttonFilters.visibility = binding.searchBar.visibility
        binding.buttonCart.visibility = binding.searchBar.visibility
        binding.roomTypeLabel.visibility = binding.searchBar.visibility
        binding.loadingViewText.visibility = binding.searchBar.visibility
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.products.value.isNullOrEmpty()) {
            viewModel.loadNextPage()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}

class ProductViewHolder(private val binding: ProductsItemProductListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var data: Product? = null
        set(newValue) {
            field = newValue
            if (newValue != null) {
                binding.titleLabel.text = newValue.title
                binding.priceLabel.text = newValue.priceString
                newValue.images.firstOrNull()?.let {
                    Glide.with(binding.imageView)
                        .load(it)
                        .placeholder(R.drawable.product_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.imageView)
                } ?: run {
                    Glide.with(binding.imageView)
                        .load(R.drawable.product_placeholder)
                        .into(binding.imageView)
                }

            }
        }
}

class ProductsAdapter(
    private val onItemClick: ((Product) -> Unit),
    private val onEndReached: (() -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val PAGE_LOAD_THRESHOLD = 4
        const val VIEW_TYPE_PRODUCT = 0
        const val VIEW_TYPE_LOADING = 1
    }

    var isLoading: Boolean = false
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    var data = listOf<Product>()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size + if (isLoading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int =
        if (position < data.size) VIEW_TYPE_PRODUCT else VIEW_TYPE_LOADING


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PRODUCT -> ProductViewHolder(
                ProductsItemProductListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ).apply {
                this.itemView.setOnClickListener {
                    this.data?.let { data ->
                        onItemClick.invoke(data)
                    }
                }
            }
            VIEW_TYPE_LOADING -> object : RecyclerView.ViewHolder(
                ProductsItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            ) {}
            else -> throw NotImplementedError("viewType $viewType not implemented")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.data = data[position]
                if (position + PAGE_LOAD_THRESHOLD >= data.size) {
                    onEndReached.invoke()
                }
                val lp = holder.itemView.layoutParams
                lp.height =
                    holder.itemView.resources.getDimensionPixelSize(
                        if (position % 2 == 0) R.dimen.products_list_item_height_1 else
                            R.dimen.products_list_item_height_2
                    )
                holder.itemView.layoutParams = lp
            }
        }
    }
}