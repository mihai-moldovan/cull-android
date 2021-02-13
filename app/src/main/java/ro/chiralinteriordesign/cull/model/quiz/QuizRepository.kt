package ro.chiralinteriordesign.cull.model.quiz

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.LocalRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.Webservice
import ro.chiralinteriordesign.cull.services.safeApiCall
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit


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
                is ResultWrapper.Success -> {
                    localRepository[this.javaClass.name] = response.value
                    val dm = App.instance.resources.displayMetrics
                    val jobs = mutableListOf<Job>()
                    for (q in response.value.questions) {
                        for (a in q.answers) {
                            jobs.add(withContext(Dispatchers.IO) {
                                async {
                                    try {
                                        Glide
                                            .with(App.instance)
                                            .load(a.photoAbsoluteURL)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .submit(dm.widthPixels, dm.heightPixels)
                                            .get()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                        }
                    }
                    for (r in response.value.results) {
                        jobs.add(withContext(Dispatchers.IO) {
                            async {
                                try {
                                    Glide
                                        .with(App.instance)
                                        .load(r.photoAbsoluteURL)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .submit(dm.widthPixels, dm.heightPixels)
                                        .get()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    }
                    jobs.joinAll()
                }
                else -> Unit
            }
            response
        }
    }

    val localQuiz get() = localRepository[this.javaClass.name] as? Quiz

    val hasLocalQuiz: Boolean = localRepository[this.javaClass.name] is Quiz

}