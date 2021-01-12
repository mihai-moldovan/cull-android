package ro.chiralinteriordesign.cull.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.databinding.ProductsListFragmentBinding
import ro.chiralinteriordesign.cull.ui.texts.TextsActivity
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.ui.auth.AuthActivity

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
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}