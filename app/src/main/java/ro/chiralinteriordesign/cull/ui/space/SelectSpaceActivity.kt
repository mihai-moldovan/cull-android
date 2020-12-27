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
import ro.chiralinteriordesign.cull.databinding.ActivitySelectSpaceBinding
import ro.chiralinteriordesign.cull.model.user.RoomType
import ro.chiralinteriordesign.cull.ui.BaseActivity
import ro.chiralinteriordesign.cull.ui.products.ProductsActivity
import ro.chiralinteriordesign.cull.utils.SimpleTextWatcher
import ro.chiralinteriordesign.cull.utils.showKeyboard

class SelectSpaceActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectSpaceBinding
    private lateinit var roomTypeViews: List<CheckedTextView>
    private lateinit var inputLayouts: List<TextInputLayout>
    private val viewModel: SelectSpaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSpaceBinding.inflate(layoutInflater)
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
            binding.lengthInput,
            binding.widthInput,
            binding.heightInput,
            binding.windowsInput,
            binding.doorsInput,
        )

        setupEdit(binding.lengthInput, binding.lengthTitle, viewModel.roomLength)
        setupEdit(binding.widthInput, binding.widthTitle, viewModel.roomWidth)
        setupEdit(binding.heightInput, binding.heightTitle, viewModel.roomHeight)
        setupEdit(binding.windowsInput, binding.windowsTitle, viewModel.roomWindows)
        setupEdit(binding.doorsInput, binding.doorsTitle, viewModel.roomDoors)

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
                startActivity(Intent(this@SelectSpaceActivity, ProductsActivity::class.java))
                finish()
            } else {
                if (viewModel.roomLength.value == 0) binding.lengthInput.error =
                    getString(R.string.space_edit_error_not_zero)
                if (viewModel.roomWidth.value == 0) binding.widthInput.error =
                    getString(R.string.space_edit_error_not_zero)
                if (viewModel.roomHeight.value == 0) binding.heightInput.error =
                    getString(R.string.space_edit_error_not_zero)
                if (viewModel.roomWindows.value == 0) binding.windowsInput.error =
                    getString(R.string.space_edit_error_not_zero)
                if (viewModel.roomDoors.value == 0) binding.doorsInput.error =
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