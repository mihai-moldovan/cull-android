package ro.chiralinteriordesign.cull.ui.auth.forgot_password

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.safeApiCall

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
class ForgotPasswordViewModel : ViewModel() {

    val email = MutableLiveData("")

    fun sendEmail(): LiveData<String?>? {
        val email = this.email.value ?: return null
        val ws = App.instance.dataRepository.webservice
        return liveData(Dispatchers.IO) {
            emit(safeApiCall { ws.forgotPassword(email) })
        }.map {
            when (it) {
                is ResultWrapper.Success -> null
                is ResultWrapper.NetworkError -> "Network error"
                is ResultWrapper.GenericError -> it.error?.detail ?: "Generic error"
            }
        }
    }
}