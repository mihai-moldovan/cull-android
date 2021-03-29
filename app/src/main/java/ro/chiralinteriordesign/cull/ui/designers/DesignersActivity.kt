package ro.chiralinteriordesign.cull.ui.designers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ro.chiralinteriordesign.cull.R

class DesignersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.designers_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.childContainer, DesignersListFragment.newInstance())
                .commitNow()
        }
    }
}