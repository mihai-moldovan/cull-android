package ro.chiralinteriordesign.cull.model.quiz

import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.safeApiCall

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */
class QuizRepository {
    private val webservice = App.instance.webservice
    private val localRepository = App.instance.localRepository

    suspend fun getQuiz(forceDownload: Boolean = false): ResultWrapper<Quiz> {
        val localData = localRepository.quiz
        return if (localData != null && !forceDownload) {
            ResultWrapper.Success(localData, true)
        } else {
            val response = safeApiCall { webservice.getQuiz() }
            when (response) {
                is ResultWrapper.Success -> localRepository.quiz = response.value
                else -> Unit
            }
            response
        }
    }

    fun hasLocalQuiz(): Boolean = App.instance.localRepository.quiz != null
}