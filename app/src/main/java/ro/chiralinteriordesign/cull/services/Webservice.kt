package ro.chiralinteriordesign.cull.services

import android.content.Context
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.model.designer.Designer
import ro.chiralinteriordesign.cull.model.quiz.Quiz
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.Product
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.model.user.User
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */


data class ErrorResponse(val detail: String?)

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T, val fromCache: Boolean) : ResultWrapper<T>()
    data class GenericError(
        val code: Int? = null,
        val error: ErrorResponse? = null,
        val json: JsonObject? = null
    ) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()

    fun errorMessage(context: Context): String? {
        return when (this) {
            is Success -> null
            is NetworkError -> context.getString(R.string.network_error)
            is GenericError -> this.error?.detail
                ?: context.getString(R.string.generic_error)
        }
    }
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
                Log.w("WEBSERVICE", throwable.response().toString())
                val body = throwable.response()?.errorBody()?.string()
                Log.w("WEBSERVICE", "Body: $body")
                val errorResponse = convertErrorBody(body)
                val json = try {
                    createGson().fromJson(body ?: "", JsonObject::class.java)
                } catch (e: java.lang.Exception) {
                    null
                }
                ResultWrapper.GenericError(code, errorResponse, json)
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

private fun convertErrorBody(jsonBody: String?): ErrorResponse? {
    return try {
        jsonBody?.let {
            createGson().fromJson(it, ErrorResponse::class.java)
        }
    } catch (exception: Exception) {
        null
    }
}

private fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        App.instance.dataRepository.userRepository.currentUser.authToken?.let { token ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Token $token")
                    .build()
            )
        } ?: chain.proceed(chain.request())
    }
    .build()

fun createWebservice(appContext: Context): Webservice = Retrofit.Builder()
    .baseUrl(appContext.resources.getString(R.string.api_url) + "/api/v1/")
    .client(getOkHttpClient())
    .addConverterFactory(
        GsonConverterFactory.create(createGson())
    )
    .build()
    .create(Webservice::class.java)


data class PaginatedResponse<T>(
    val count: Int,
    val next: String,
    val previouse: String,
    val results: List<T>,
)

interface Webservice {

    @GET("quiz/")
    suspend fun getQuiz(): Quiz

    @FormUrlEncoded
    @POST("auth/login/")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): User

    @FormUrlEncoded
    @POST("auth/register/")
    suspend fun register(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): User

    @FormUrlEncoded
    @POST("auth/save/")
    suspend fun save(
        @Field("first_name") firstName: String?,
        @Field("last_name") lastName: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("quiz") iquizResultId: Int?
    ): User

    @GET("text/{key}/")
    suspend fun getText(@Path("key") key: String): Text

    @FormUrlEncoded
    @POST("password_reset/")
    suspend fun forgotPassword(
        @Field("email") email: String
    ): Boolean

    @GET("shop/products/")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("query") query: String?,
        @Query("product_type") productType: String?,
        @Query("min_price") minPrice: Float?,
        @Query("max_price") maxPrice: Float?,
        @Query("color") color: String?,
        @Query("material") material: String?,
        @Query("room_type") roomType: String?,
        @Query("room_area") roomArea: Int?,
        @Query("style_result") styleResult: String?,
    ): PaginatedResponse<Product>

    @GET("shop/carts/")
    suspend fun getCarts(): List<Cart>


    @FormUrlEncoded
    @POST("password_reset/confirm/")
    suspend fun resetPassword(
        @Field("password") password: String,
        @Field("token") token: String,
    ): Gson

    @GET("designers/")
    suspend fun getDesigners(): List<Designer>
}