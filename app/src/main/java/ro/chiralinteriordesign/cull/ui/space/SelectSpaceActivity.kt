package ro.chiralinteriordesign.cull.ui.space

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.SelectSpaceActivityBinding
import ro.chiralinteriordesign.cull.model.user.RoomType
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.products.ProductsActivity
import ro.chiralinteriordesign.cull.ui.splash.SplashActivity
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.showKeyboard

class SelectSpaceActivity : BaseActivity() {

    private lateinit var binding: SelectSpaceActivityBinding
    private lateinit var roomTypeViews: List<CheckedTextView>
    private lateinit var inputLayouts: List<TextInputLayout>
    private val viewModel: SelectSpaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SelectSpaceActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        roomTypeViews = listOf(
            binding.roomTypeLiving,
            binding.roomTypeDining,
            binding.roomTypeLobby,
            binding.roomTypeOffice,
            binding.roomTypeBedroom,
            binding.roomTypeChildroom,
        )
        roomTypeViews.forEach { setupCheckBox(it) }

        inputLayouts = listOf(
            binding.surfaceInput,
        )

        setupEdit(binding.surfaceInput, binding.surfaceTitle, viewModel.roomArea)

        viewModel.roomType.observe(this) { type ->
            roomTypeViews.forEachIndexed { i, v ->
                v.isChecked = type.ordinal == i
                v.setTextColor(getColorStateList(R.color.white_gold_selected))
            }
        }

        viewModel.room.observe(this) {
            //do nothing. we just need an observer for the mediator to work
        }

        binding.btnContinue.setOnClickListener {
            inputLayouts.forEach { it.error = null }
            if (viewModel.room.value?.isValid == true) {
                viewModel.saveRoom()
                SplashActivity.showInitialActivity(this)
            } else {
                if (viewModel.roomArea.value == 0) binding.surfaceInput.error =
                    getString(R.string.space_edit_error_not_zero)
                if (viewModel.roomType.value == null) {
                    roomTypeViews.forEach {
                        it.setTextColor(getColor(com.google.android.material.R.color.design_error))
                    }
                }
            }
        }
    }


    private fun setupEdit(
        inputLayout: TextInputLayout,
        titleView: TextView,
        liveData: MutableLiveData<Int>
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
                liveData.postValue(s?.toString()?.toIntOrNull() ?: 0)
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


    private fun setupCheckBox(view: CheckedTextView) {
        view.setOnClickListener {
            viewModel.roomType.postValue(RoomType.values()[roomTypeViews.indexOf(view)])
        }
    }
}