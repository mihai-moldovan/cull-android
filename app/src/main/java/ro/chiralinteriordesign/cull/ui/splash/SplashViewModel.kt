package ro.chiralinteriordesign.cull.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.model.quiz.QuizRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper


/**
 * Created by Mihai Moldovan on 23/12/2020.
 */
class SplashViewModel : ViewModel() {

    private val quizRepo = QuizRepository()

    fun ensureAllLoaded() = liveData(Dispatchers.IO) {
        if (quizRepo.hasLocalQuiz()) {
            emit(true)
        } else {
            when (quizRepo.getQuiz(false)) {
                is ResultWrapper.Success -> emit(true)
                else -> emit(false)
            }
        }
    }

}