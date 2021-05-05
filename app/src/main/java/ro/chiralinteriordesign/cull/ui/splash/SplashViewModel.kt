package ro.chiralinteriordesign.cull.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.quiz.QuizRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper


/**
 * Created by Mihai Moldovan on 23/12/2020.
 */
class SplashViewModel : ViewModel() {

    private val dataRepo = App.instance.dataRepository

    fun ensureAllLoaded() = liveData(Dispatchers.IO) {
        val result = dataRepo.reloadDataIfNeeded()
        if (dataRepo.userRepository.isLoggedIn) {
            dataRepo.loadUserData()
        }
        emit(result || dataRepo.hasLocalData)
    }
}