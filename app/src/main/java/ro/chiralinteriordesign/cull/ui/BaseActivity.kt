package ro.chiralinteriordesign.cull.ui

import androidx.appcompat.app.AppCompatActivity
import ro.chiralinteriordesign.cull.R

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
open class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        if ((supportFragmentManager.findFragmentById(R.id.childContainer) as?
                    BackButtonListener)?.onBackPressed() != true
        ) {
            super.onBackPressed()
        }
    }
}

interface BackButtonListener {
    fun onBackPressed(): Boolean
}