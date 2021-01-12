package ro.chiralinteriordesign.cull.ui.auth.reset_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
class ResetPasswordViewModel : ViewModel() {

    val password = MutableLiveData("")
    val password2 = MutableLiveData("")

    fun resetPassword(): LiveData<Boolean> {
        val data = MutableLiveData(false)
        return data
    }
}