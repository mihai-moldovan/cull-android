package ro.chiralinteriordesign.cull.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Constants
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.model.quiz.QuizRepository
import ro.chiralinteriordesign.cull.model.user.UserRepository

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
class DataRepository(val appContext: Context) {

    companion object {
        const val TAG = "DataRepo"
    }

    private val localRepository: LocalRepository by lazy { LocalRepository(appContext) }
    private val webservice: Webservice by lazy { createWebservice(appContext) }

    val quizRepository: QuizRepository by lazy { QuizRepository(localRepository, webservice) }
    val userRepository: UserRepository by lazy { UserRepository(localRepository, webservice) }

    suspend fun reloadDataIfNeeded(): Boolean {
        val timestamp = App.instance.preferences.getLong(Preferences.Key.CACHE_TIMESTAMP, 0L)
        if (System.currentTimeMillis() - timestamp > Constants.CACHE_UPDATE_PERIOD) {
            return withContext(Dispatchers.IO) {
                val result = quizRepository.getQuiz(true)
                when (result) {
                    is ResultWrapper.Success -> {
                        App.instance.preferences.put(
                            Preferences.Key.CACHE_TIMESTAMP,
                            System.currentTimeMillis()
                        )
                        Log.i(TAG, "Downloaded data")
                        true
                    }
                    is ResultWrapper.GenericError -> {
                        Log.e(TAG, "Error downloading data: ${result.error?.detail}")
                        false
                    }
                    is ResultWrapper.NetworkError -> {
                        Log.e(TAG, "Error downloading data: Network error")
                        false
                    }
                }
            }
        } else {
            Log.d(TAG, "too soon to download")
            return true
        }
    }

    val hasLocalData: Boolean = quizRepository.hasLocalQuiz
}