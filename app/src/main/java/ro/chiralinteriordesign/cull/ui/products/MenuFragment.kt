package ro.chiralinteriordesign.cull.ui.products

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
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
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.ui.auth.AuthActivity
import ro.chiralinteriordesign.cull.ui.cart.CartActivity
import ro.chiralinteriordesign.cull.ui.designers.DesignersActivity
import ro.chiralinteriordesign.cull.ui.quiz.QuizActivity
import ro.chiralinteriordesign.cull.ui.space.SelectSpaceActivity
import ro.chiralinteriordesign.cull.ui.splash.SplashActivity
import ro.chiralinteriordesign.cull.ui.texts.TextsActivity

/**
 * Created by Mihai Moldovan on 01/01/2021.
 */

private const val ARG_HAS_LISTENER = "has_listener"

class MenuFragment : DialogFragment() {

    companion object {
        const val MENU_RESULT = "menu_result"
        const val TAG = "Menu"
        fun newInstance(hasListener: Boolean = false): MenuFragment =
            MenuFragment().apply {
                this.arguments = Bundle().apply {
                    putBoolean(ARG_HAS_LISTENER, hasListener)
                }
            }


        val loggedInMenu = listOf(
            MenuItem.ACCOUNT,
            MenuItem.SAVED_LIST,
            MenuItem.CHANGE_SPACE,
            MenuItem.REDO_TEST,
            MenuItem.CONTACT_DESIGNER,
            MenuItem.TERMS,
            MenuItem.PRIVACY,
            MenuItem.LOGOUT,
        )

        val notLoggedInMenu = listOf(
            MenuItem.LOGIN,
            MenuItem.SAVED_LIST,
            MenuItem.CHANGE_SPACE,
            MenuItem.REDO_TEST,
            MenuItem.CONTACT_DESIGNER,
            MenuItem.TERMS,
            MenuItem.PRIVACY,
        )
    }

    enum class MenuItem(@StringRes val textRes: Int) {
        LOGIN(R.string.menu_login),
        ACCOUNT(R.string.menu_account),
        SAVED_LIST(R.string.menu_saved_list),
        CHANGE_SPACE(R.string.menu_change_room),
        REDO_TEST(R.string.menu_redo_test),
        CONTACT_DESIGNER(R.string.menu_contact_designer),
        TERMS(R.string.menu_terms),
        PRIVACY(R.string.menu_privacy),
        LOGOUT(R.string.menu_logout),
    }

    private var binding: MenuFragmentBinding? = null
    private var hasListener = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasListener = arguments?.getBoolean(ARG_HAS_LISTENER) ?: false
    }

    override fun getTheme(): Int {
        return R.style.Theme_CULL_Dialog
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

        binding.parentView.setOnClickListener {
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
        if (hasListener) {
            setFragmentResult(TAG, Bundle().apply {
                putSerializable(MENU_RESULT, item)
            })
        } else {
            when (item) {
                MenuItem.ACCOUNT -> {
                }
                MenuItem.TERMS -> startActivity(
                    TextsActivity.getIntent(
                        requireContext(),
                        getString(R.string.menu_terms),
                        textKey = Text.Key.TERMS,
                    )
                )
                MenuItem.PRIVACY -> startActivity(
                    TextsActivity.getIntent(
                        requireContext(),
                        getString(R.string.menu_privacy),
                        textKey = Text.Key.PRIVACY,
                    )
                )
                MenuItem.LOGOUT -> {
                    App.instance.dataRepository.userRepository.logout()
                    startActivity(Intent(requireContext(), SplashActivity::class.java))
                    requireActivity().finish()
                }
                MenuItem.LOGIN -> startActivity(
                    Intent(requireContext(), AuthActivity::class.java)
                )
                MenuItem.REDO_TEST -> startActivity(
                    Intent(requireContext(), QuizActivity::class.java)
                )
                MenuItem.CONTACT_DESIGNER -> startActivity(
                    Intent(requireContext(), DesignersActivity::class.java)
                )
                MenuItem.CHANGE_SPACE -> startActivity(
                    Intent(requireContext(), SelectSpaceActivity::class.java)
                )
                MenuItem.SAVED_LIST -> startActivity(
                    Intent(requireContext(), CartActivity::class.java)
                )
            }
        }
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