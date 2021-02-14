package ro.chiralinteriordesign.cull.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ProductsItemLoadingBinding
import ro.chiralinteriordesign.cull.databinding.ProductsItemProductListBinding
import ro.chiralinteriordesign.cull.databinding.ProductsListFragmentBinding
import ro.chiralinteriordesign.cull.model.shop.Product
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.ui.auth.AuthActivity
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity
import ro.chiralinteriordesign.cull.ui.texts.TextsActivity
import ro.chiralinteriordesign.cull.utils.hideKeyboard
import ro.chiralinteriordesign.cull.utils.pushFragment

class ProductsListFragment : Fragment() {

    private var binding: ProductsListFragmentBinding? = null
    val adapter = ProductsAdapter({
        parentFragmentManager.pushFragment(ProductDetailsFragment.newInstance(it))
    },
        {
            viewModel.loadNextPage()
        })

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
        val binding = binding ?: return
        binding.buttonMenu.setOnClickListener {
            val fr = MenuFragment.newInstance()
            fr.showNow(parentFragmentManager, MenuFragment.TAG)
            fr.setFragmentResultListener(MenuFragment.TAG) { _, bundle ->
                (bundle.getSerializable(MenuFragment.MENU_RESULT) as? MenuFragment.MenuItem)?.let { item ->
                    when (item) {
                        MenuFragment.MenuItem.ACCOUNT -> {
                        }
                        MenuFragment.MenuItem.ORDERS -> {
                        }
                        MenuFragment.MenuItem.TERMS -> startActivity(
                            TextsActivity.getIntent(
                                requireContext(),
                                getString(R.string.menu_terms),
                                textKey = Text.Key.TERMS,
                            )
                        )
                        MenuFragment.MenuItem.PRIVACY -> startActivity(
                            TextsActivity.getIntent(
                                requireContext(),
                                getString(R.string.menu_privacy),
                                textKey = Text.Key.PRIVACY,
                            )
                        )
                        MenuFragment.MenuItem.LOGOUT -> {
                            App.instance.dataRepository.userRepository.logout()
                        }
                        MenuFragment.MenuItem.LOGIN -> startActivity(
                            Intent(requireContext(), AuthActivity::class.java)
                        )
                        MenuFragment.MenuItem.REDO_TEST -> startActivity(
                            Intent(requireContext(), QuizActivity::class.java)
                        )
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            val binding = this.binding ?: return@Observer
            if (isLoading && adapter.data.isEmpty()) {
                binding.loadingView.visibility = View.VISIBLE
                binding.productView.visibility = View.GONE
            } else {
                binding.loadingView.visibility = View.GONE
                binding.productView.visibility = View.VISIBLE
            }
            adapter.isLoading = isLoading
        })


        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.data = it
        }

        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchQuery(v.text.toString())
                v.hideKeyboard()
                viewModel.loadNextPage()
                true
            } else {
                false
            }
        }

        viewModel.roomStyle.observe(viewLifecycleOwner) {
            binding.roomTypeLabel.text = it
        }
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
                        .into(binding.imageView)
                } ?: run {
                    Glide.with(binding.imageView)
                        .load(R.drawable.logo_auth)
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
            }
        }
    }
}