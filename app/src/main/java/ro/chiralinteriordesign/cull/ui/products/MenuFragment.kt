package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.MenuFragmentBinding

/**
 * Created by Mihai Moldovan on 01/01/2021.
 */
class MenuFragment : DialogFragment() {

    companion object {
        const val MENU_RESULT = "menu_result"
        const val TAG = "Menu"
        fun newInstance(): MenuFragment = MenuFragment()

        val loggedInMenu = listOf(
            MenuItem.ACCOUNT,
            MenuItem.ORDERS,
            MenuItem.TERMS,
            MenuItem.PRIVACY,
            MenuItem.LOGOUT,
        )

        val notLoggedInMenu = listOf(
            MenuItem.LOGIN,
            MenuItem.TERMS,
            MenuItem.PRIVACY,
        )
    }

    enum class MenuItem(@StringRes val textRes: Int) {
        ACCOUNT(R.string.menu_account),
        ORDERS(R.string.menu_orders),
        TERMS(R.string.menu_terms),
        PRIVACY(R.string.menu_privacy),
        LOGOUT(R.string.menu_logout),
        LOGIN(R.string.menu_login),
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
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.btnClose.setOnClickListener {
            this.dismiss()
        }
        if (App.instance.dataRepository.userRepository.isLoggedIn) {
            binding.recyclerView.adapter = Adapter(loggedInMenu)
        } else {
            binding.recyclerView.adapter = Adapter(notLoggedInMenu)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun onMenuItemClicked(item: MenuItem) {
        setFragmentResult(TAG, Bundle().apply {
            putSerializable(MENU_RESULT, item)
        })
        dismiss()
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class Adapter(private val items: List<MenuItem>) : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
            ).also { vh ->
                vh.itemView.setOnClickListener {
                    onMenuItemClicked(items[vh.adapterPosition])
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.itemView as? TextView)?.text =
                requireContext().getString(items[position].textRes)
        }
    }
}