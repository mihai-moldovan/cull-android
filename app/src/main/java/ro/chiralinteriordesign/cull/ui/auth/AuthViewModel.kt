package ro.chiralinteriordesign.cull.ui.auth

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.safeApiCall

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val password2 = MutableLiveData("")
    val termsAgreed = MutableLiveData(false)

    private val userRepo = App.instance.dataRepository.userRepository

    fun login(email: String, password: String): LiveData<String?> {
        return liveData(Dispatchers.IO) {
            emit(userRepo.login(email, password))
        }.map { it.errorMessage(getApplication()) }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): LiveData<String?> {
        return liveData(Dispatchers.IO) {
            emit(userRepo.register(firstName, lastName, email, password) )
        }.map { it.errorMessage(getApplication()) }
    }

}