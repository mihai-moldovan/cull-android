package ro.chiralinteriordesign.cull.model.designer

import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.RequestBody
import ro.chiralinteriordesign.cull.services.*


/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

class DesignerRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun getDesigners(): ResultWrapper<List<Designer>> {
        return safeApiCall { webservice.getDesigners() }
    }

    suspend fun contactDesigners(model: ContactDesigners): Boolean {
        val response = safeApiCall {
            val body = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                createGson().toJson(model)
            )
            webservice.contactDesigners(body)
        }
        return when (response) {
            is ResultWrapper.Success -> {
                true
            }
            else -> false
        }
    }
}