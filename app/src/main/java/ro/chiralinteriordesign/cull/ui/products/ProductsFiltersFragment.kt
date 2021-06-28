package ro.chiralinteriordesign.cull.ui.products

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.innovattic.rangeseekbar.RangeSeekBar
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ProductsFiltersFragmentBinding
import ro.chiralinteriordesign.cull.databinding.ProductsFiltersItemColorBinding
import ro.chiralinteriordesign.cull.databinding.ProductsFiltersItemTextBinding
import ro.chiralinteriordesign.cull.model.shop.FilterItem
import ro.chiralinteriordesign.cull.model.shop.Product
import ro.chiralinteriordesign.cull.model.shop.ProductFilters

class ProductsFiltersFragment : Fragment() {

    companion object {
        val REQUEST_KEY = "ProductsFiltersFragmentRequest"

        @JvmStatic
        fun newInstance(filterValues: ProductFilters?) = ProductsFiltersFragment().apply {
            arguments = Bundle().apply {
                filterValues?.let { putSerializable(ARG_FILTERS, it) }
            }
        }

        const val ARG_FILTERS = "filters"
    }

    private var binding: ProductsFiltersFragmentBinding? = null
    private val viewModel: ProductsFiltersViewModel by viewModels()
    private var originalFilters = ProductFilters()

    private val categoriesAdapter = TextItemAdapter {

    }

    private val materialsAdapter = TextItemAdapter {

    }
    private val colorsAdapter = ColorItemAdapter {
        updateColorSelectionLabels()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (arguments?.getSerializable(ARG_FILTERS) as? ProductFilters)?.let { originalFilters = it }
    }


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.categoriesRecyclerView.adapter = categoriesAdapter
        binding.materialRecyclerView.adapter = materialsAdapter
        binding.colorsRecyclerView.adapter = colorsAdapter
        viewModel.filtersData.observe(viewLifecycleOwner) {
            categoriesAdapter.data = it?.categories
            materialsAdapter.data = it?.materials
            colorsAdapter.data = it?.colors
            binding.priceSeekBar.max = it?.let { it.maxPrice - it.minPrice + 1 } ?: 1000
            updatePriceLabels()
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnApply.setOnClickListener {
            val offset = viewModel.filtersData.value?.minPrice ?: 0
            val filterValues = originalFilters.copy(
                productType = categoriesAdapter.selection.joinToString(",") { it },
                color = colorsAdapter.selection.joinToString(",") { it },
                material = materialsAdapter.selection.joinToString(",") { it },
                minPrice = binding.priceSeekBar.getMinThumbValue() + offset,
                maxPrice = binding.priceSeekBar.getMaxThumbValue() + offset,
            )
            setFragmentResult(REQUEST_KEY, Bundle().apply {
                putSerializable(ARG_FILTERS, filterValues)
            })
            parentFragmentManager.popBackStack()
        }

        binding.priceSeekBar.seekBarChangeListener = object : RangeSeekBar.SeekBarChangeListener {
            override fun onStartedSeeking() {

            }

            override fun onStoppedSeeking() {

            }

            override fun onValueChanged(minThumbValue: Int, maxThumbValue: Int) {
                updatePriceLabels()
            }
        }
        binding.priceSeekBar.minRange = 50

        originalFilters.let {
            categoriesAdapter.selection = it.productType?.split("\\s*,\\s*".toRegex())?.toMutableSet() ?: mutableSetOf()
            materialsAdapter.selection = it.material?.split("\\s*,\\s*".toRegex())?.toMutableSet() ?: mutableSetOf()
            colorsAdapter.selection = it.color?.split("\\s*,\\s*".toRegex())?.toMutableSet() ?: mutableSetOf()
            val offset = viewModel.filtersData.value?.minPrice ?: 0
            it.minPrice?.let { binding.priceSeekBar.setMinThumbValue(it + offset) }
            it.maxPrice?.let { binding.priceSeekBar.setMaxThumbValue(it + offset) }
            updatePriceLabels()
        }
    }

    private fun updatePriceLabels() {
        val binding = binding ?: return
        val offset = viewModel.filtersData.value?.minPrice ?: 0
        binding.pricesSelectionView.text =
            resources.getString(
                R.string.filter_price_range_format,
                binding.priceSeekBar.getMinThumbValue() + offset,
                binding.priceSeekBar.getMaxThumbValue() + offset
            )
    }

    private fun updateColorSelectionLabels() {
        val binding = binding ?: return
        binding.colorsSelectionTextView.text = colorsAdapter.selection.mapNotNull { selectedKey ->
            colorsAdapter.data?.firstOrNull { it.key == selectedKey }
        }.joinToString(", ") { it.label }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFilters()
    }

    abstract class GenericViewHolder(open val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        open var data: FilterItem? = null
        open var isSelected: Boolean = false
    }

    class TextItemViewHolder(override val binding: ProductsFiltersItemTextBinding) : GenericViewHolder(binding) {

        override var data: FilterItem? = null
            set(value) {
                field = value
                value?.let {
                    binding.textView.text = it.label
                }
            }

        override var isSelected: Boolean = false
            set(value) {
                field = value
                val color = ContextCompat.getColor(binding.textView.context, if (value) R.color.gold else R.color.white)
                binding.textView.setTextColor(color)
                binding.iconView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
    }

    class ColorItemViewHolder(override val binding: ProductsFiltersItemColorBinding) : GenericViewHolder(binding) {

        override var data: FilterItem? = null
            set(value) {
                field = value
                value?.let {
                    it.photo?.let { photo ->
                        Glide.with(binding.iconView)
                            .load(photo)
                            .circleCrop()
                            .into(binding.iconView)
                    } ?: run {
                        Glide.with(binding.iconView)
                            .clear(binding.iconView)
                    }
                }
            }

        override var isSelected: Boolean = false
            set(value) {
                field = value
                binding.selectionView.visibility = if (value) View.VISIBLE else View.INVISIBLE
            }
    }


    abstract class GenericFilterAdapter<VH : GenericViewHolder>(
        private val onSelectionChanged: () -> Unit
    ) : RecyclerView.Adapter<VH>() {

        var data: List<FilterItem>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        var selection = mutableSetOf<String>()
            set(newValue) {
                field = newValue
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        abstract fun createViewHolder(parent: ViewGroup): VH

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return createViewHolder(parent).apply {
                this.binding.root.setOnClickListener {
                    this.data?.let {
                        if (!selection.add(it.key)) {
                            selection.remove(it.key)
                        }
                        notifyItemChanged(this.adapterPosition)
                        onSelectionChanged.invoke()
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            data?.let {
                holder.data = it[position]
                holder.isSelected = selection.contains(it[position].key)
            }
        }
    }


    class TextItemAdapter(onSelectionChanged: () -> Unit) : GenericFilterAdapter<TextItemViewHolder>(onSelectionChanged) {

        override fun createViewHolder(parent: ViewGroup): TextItemViewHolder =
            TextItemViewHolder(ProductsFiltersItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ColorItemAdapter(onSelectionChanged: () -> Unit) : GenericFilterAdapter<ColorItemViewHolder>(onSelectionChanged) {

        override fun createViewHolder(parent: ViewGroup): ColorItemViewHolder =
            ColorItemViewHolder(ProductsFiltersItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}

