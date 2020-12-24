package ro.chiralinteriordesign.cull.model.quiz

import ro.chiralinteriordesign.cull.services.*

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

class QuizRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun getQuiz(forceDownload: Boolean = false): ResultWrapper<Quiz> {
        val localData = localRepository[this.javaClass.name] as? Quiz
        return if (localData != null && !forceDownload) {
            ResultWrapper.Success(localData, true)
        } else {
            val response = safeApiCall { webservice.getQuiz() }
            when (response) {
                is ResultWrapper.Success -> localRepository[this.javaClass.name] = response.value
                else -> Unit
            }
            response
        }
    }

    val hasLocalQuiz: Boolean = localRepository[this.javaClass.name] is Quiz

}