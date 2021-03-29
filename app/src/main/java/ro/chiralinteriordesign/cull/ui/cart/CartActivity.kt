package ro.chiralinteriordesign.cull.ui.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ro.chiralinteriordesign.cull.R

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.childContainer, CartListFragment.newInstance())
                .commitNow()
        }
    }
}