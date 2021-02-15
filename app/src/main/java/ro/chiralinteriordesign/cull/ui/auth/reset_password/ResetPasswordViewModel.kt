package ro.chiralinteriordesign.cull.ui.auth.reset_password

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.safeApiCall

/**
 * Created by Mihai Moldovan on 12/01/2021.
 */
class ResetPasswordViewModel(app: Application) : AndroidViewModel(app) {

    private val webService = App.instance.dataRepository.webservice
    val password = MutableLiveData("")
    val password2 = MutableLiveData("")
    val isLoading = MutableLiveData(false)

    fun resetPassword(token: String) = liveData(Dispatchers.IO) {
        val password = password.value
        val password2 = password2.value
        val app = getApplication() as Context
        if (password == null || password2 == null) {
            emit(app.getString(R.string.auth_reset_pass_missing_fields))
            return@liveData
        }
        if (password != password2) {
            emit(app.getString(R.string.auth_reset_pass_no_match))
            return@liveData
        }
        isLoading.postValue(true)
        val result = safeApiCall { webService.resetPassword(password, token) }
        isLoading.postValue(false)
        when (result) {
            is ResultWrapper.Success -> emit(null)
            is ResultWrapper.NetworkError -> emit(app.getString(R.string.network_error))
            is ResultWrapper.GenericError -> {
                emit(result.error?.detail ?: result.json?.let { obj ->
                    try {
                        obj.getAsJsonArray("password").joinToString("\n") { it.asString }
                    } catch (ex: Exception) {
                        null
                    }
                } ?: app.getString(R.string.generic_error))
            }
        }
    }
}