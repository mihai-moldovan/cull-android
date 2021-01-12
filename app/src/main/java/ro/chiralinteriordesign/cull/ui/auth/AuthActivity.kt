package ro.chiralinteriordesign.cull.ui.auth

import android.os.Bundle
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.ui.BaseActivity

class AuthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.childContainer, LoginFragment.newInstance())
                .commitNow()
        }
    }
}