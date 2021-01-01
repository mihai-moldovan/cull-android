package ro.chiralinteriordesign.cull.model.text

import ro.chiralinteriordesign.cull.services.LocalRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.Webservice
import ro.chiralinteriordesign.cull.services.safeApiCall

/**
 * Created by Mihai Moldovan on 31/12/2020.
 */
class TextRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun getText(key: Text.Key, forceDownload: Boolean = false): ResultWrapper<Text> {
        val localData = localRepository[this.javaClass.name] as? Text
        return if (localData != null && !forceDownload) {
            ResultWrapper.Success(localData, true)
        } else {
            val response = safeApiCall { webservice.getText(key.key) }
            when (response) {
                is ResultWrapper.Success -> {
                    localRepository[this.javaClass.name] = response.value
                }
                else -> Unit
            }
            response
        }
    }

}