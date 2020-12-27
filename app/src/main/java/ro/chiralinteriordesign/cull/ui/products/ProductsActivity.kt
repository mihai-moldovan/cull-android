package ro.chiralinteriordesign.cull.ui.products

import android.os.Bundle
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.ui.BaseActivity

class ProductsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.products_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.childContainer, ProductsListFragment.newInstance())
                .commitNow()
        }
    }
}