package ro.chiralinteriordesign.cull.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.CartListFragmentBinding
import ro.chiralinteriordesign.cull.databinding.CartListItemBinding
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.utils.pushFragment

class CartListFragment : Fragment() {

    companion object {
        fun newInstance() = CartListFragment()
    }

    private var binding: CartListFragmentBinding? = null
    private val viewModel: CartListViewModel by viewModels()
    private val adapter = Adapter({ _, position ->
        parentFragmentManager.pushFragment(CartFragment.newInstance(position))
    }, { item, _ ->
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_confirmation_title)
            .setMessage(R.string.alert_confirmation_delete_cart)
            .setPositiveButton(R.string.alert_yes) { _, _ ->
                viewModel.deleteCart(item)
            }
            .setNegativeButton(R.string.alert_no, null)
            .show()
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CartListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.recyclerView.adapter = adapter

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.navBar.titleView.setText(R.string.menu_saved_list)

        viewModel.cartList.observe(viewLifecycleOwner) {
            adapter.data = it
        }
    }


    class CartViewHolder(val binding: CartListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var item: Cart? = null
            set(newValue) {
                field = newValue
                newValue?.let {
                    binding.titleView.text = it.name
                    binding.subtitleView.text =
                        binding.subtitleView.resources.getQuantityString(R.plurals.cart_count_format, it.lineItems.size, it.lineItems.size)
                    binding.statusView.visibility = if (it.isSent) View.VISIBLE else View.GONE
                }
            }
    }

    class Adapter(
        private val onItemClick: ((Cart, Int) -> Unit),
        private val onItemDelete: ((Cart, Int) -> Unit),
    ) : RecyclerView.Adapter<CartViewHolder>() {

        var data: List<Cart>? = null
            set(newValue) {
                field = newValue
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
            return CartViewHolder(CartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                .also { vh ->
                    vh.itemView.setOnClickListener { vh.item?.let { onItemClick(it, vh.adapterPosition) } }
                    vh.binding.buttonDelete.setOnClickListener { vh.item?.let { onItemDelete(it, vh.adapterPosition) } }
                }
        }

        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            holder.item = data?.get(position)
        }
    }
}