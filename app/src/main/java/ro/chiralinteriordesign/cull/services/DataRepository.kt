package ro.chiralinteriordesign.cull.services

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.Constants
import ro.chiralinteriordesign.cull.Preferences
import ro.chiralinteriordesign.cull.model.designer.DesignerRepository
import ro.chiralinteriordesign.cull.model.quiz.QuizRepository
import ro.chiralinteriordesign.cull.model.shop.ShopRepository
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.model.text.TextRepository
import ro.chiralinteriordesign.cull.model.user.UserRepository

/**
 * Created by Mihai Moldovan on 24/12/2020.
 */
class DataRepository(private val appContext: Context) {

    companion object {
        const val TAG = "DataRepo"
    }

    private val localRepository: LocalRepository by lazy { LocalRepository(appContext) }
    val webservice: Webservice by lazy { createWebservice(appContext) }

    val quizRepository: QuizRepository by lazy { QuizRepository(localRepository, webservice) }
    val userRepository: UserRepository by lazy { UserRepository(localRepository, webservice) }
    val textRepository: TextRepository by lazy { TextRepository(localRepository, webservice) }
    val productRepository: ShopRepository by lazy { ShopRepository(localRepository, webservice) }
    val designersRepository: DesignerRepository by lazy { DesignerRepository(localRepository, webservice) }

    suspend fun reloadDataIfNeeded(): Boolean {
        val timestamp = App.instance.preferences.getLong(Preferences.Key.CACHE_TIMESTAMP, 0L)
        if (System.currentTimeMillis() - timestamp > Constants.CACHE_UPDATE_PERIOD) {
            return withContext(Dispatchers.IO) {

                val results = awaitAll(
                    async { quizRepository.getQuiz(true) },
                    async { textRepository.getText(Text.Key.TERMS, true) },
                    async { textRepository.getText(Text.Key.PRIVACY, true) },
                )
                when {
                    results.all { it is ResultWrapper.Success } -> {
                        App.instance.preferences.put(
                            Preferences.Key.CACHE_TIMESTAMP,
                            System.currentTimeMillis()
                        )
                        Log.i(TAG, "Downloaded data")
                        true
                    }
                    results.any { it is ResultWrapper.GenericError } -> {
                        Log.e(TAG, "Error downloading data.")
                        false
                    }
                    results.any { it is ResultWrapper.NetworkError } -> {
                        Log.e(TAG, "Error downloading data: Network error")
                        false
                    }
                    else -> false
                }
            }
        } else {
            Log.d(TAG, "too soon to download")
            return true
        }
    }

    val hasLocalData: Boolean = quizRepository.hasLocalQuiz

    suspend fun loadUserData(): Boolean {
//        productRepository.loadCarts(true)
        return true
    }
}