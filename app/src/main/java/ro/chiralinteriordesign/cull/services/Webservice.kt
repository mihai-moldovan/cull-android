package ro.chiralinteriordesign.cull.services

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.model.quiz.Quiz
import ro.chiralinteriordesign.cull.model.user.User
import java.io.IOException

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */


data class ErrorResponse(val detail: String?)

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T, val fromCache: Boolean) : ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return try {
        ResultWrapper.Success(apiCall.invoke(), false)
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            is HttpException -> {
                val code = throwable.code()
                val errorResponse = convertErrorBody(throwable)
                ResultWrapper.GenericError(code, errorResponse)
            }
            else -> {
                ResultWrapper.GenericError(null, null)
            }
        }
    }
}

private fun createGson(): Gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .setDateFormat("")
    .create()

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.string()?.let {
            createGson().fromJson(it, ErrorResponse::class.java)
        }
    } catch (exception: Exception) {
        null
    }
}

fun createWebservice(appContext: Context): Webservice = Retrofit.Builder()
    .baseUrl(appContext.resources.getString(R.string.api_url) + "/api/v1/")
    .addConverterFactory(
        GsonConverterFactory.create(createGson())
    )
    .build()
    .create(Webservice::class.java)


interface Webservice {

    @GET("quiz/")
    suspend fun getQuiz(): Quiz

    @POST("user/login/")
    suspend fun login(email: String, password: String): User

    @POST("user/register/")
    suspend fun register(firstName: String, lastName: String, email: String, password: String): User

    @POST("user/save/")
    suspend fun save(
        firstName: String?,
        lastName: String?,
        email: String?,
        password: String?,
        quizResultId: Int?
    ): User

}