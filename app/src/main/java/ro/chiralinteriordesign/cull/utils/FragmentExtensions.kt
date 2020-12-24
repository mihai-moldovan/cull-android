package ro.chiralinteriordesign.cull.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ro.chiralinteriordesign.cull.R

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
fun FragmentManager.pushFragment(fr: Fragment, tag: String? = null) =
    this.beginTransaction()
        .addToBackStack(tag ?: fr.javaClass.name)
        .setCustomAnimations(R.anim.push_enter, R.anim.push_exit, R.anim.pop_enter, R.anim.pop_exit)
        .replace(R.id.childContainer, fr, tag ?: fr.javaClass.name)
        .commit()
