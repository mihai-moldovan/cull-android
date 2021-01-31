package ro.chiralinteriordesign.cull.ui.auth.forgot_password

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.safeApiCall
import ro.chiralinteriordesign.cull.R

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
class ForgotPasswordViewModel(val app: Application) : AndroidViewModel(app) {

    val email = MutableLiveData("")

    fun sendEmail(): LiveData<String?>? {
        val email = this.email.value ?: return null
        val ws = App.instance.dataRepository.webservice
        return liveData(Dispatchers.IO) {
            emit(safeApiCall { ws.forgotPassword(email) })
        }.map {
            when (it) {
                is ResultWrapper.Success -> null
                is ResultWrapper.NetworkError -> app.getString(R.string.network_error)
                is ResultWrapper.GenericError -> it.error?.detail
                    ?: app.getString(R.string.generic_error)
            }
        }
    }
}