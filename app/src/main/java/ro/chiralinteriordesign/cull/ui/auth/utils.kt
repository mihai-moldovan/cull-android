package ro.chiralinteriordesign.cull.ui.auth

import android.text.Editable
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.showKeyboard

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
fun setupAuthEdit(
    inputLayout: TextInputLayout,
    titleView: TextView,
    liveData: MutableLiveData<String>
) {
    val editText = inputLayout.editText ?: return
    inputLayout.setOnClickListener {
        editText.requestFocus()
    }
    titleView.setOnClickListener {
        editText.requestFocus()
    }
    editText.addTextChangedListener(object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            liveData.postValue(s?.toString())
        }
    })
    editText.setOnFocusChangeListener { _, hasFocus ->
        titleView.isSelected = hasFocus
        inputLayout.error = null
        if (hasFocus) {
            editText.showKeyboard()
        }
    }
}