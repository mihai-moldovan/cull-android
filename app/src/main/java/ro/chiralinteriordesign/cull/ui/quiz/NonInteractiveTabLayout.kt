package ro.chiralinteriordesign.cull.ui.quiz

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.tabs.TabLayout

class NonInteractiveTabLayout : TabLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

}