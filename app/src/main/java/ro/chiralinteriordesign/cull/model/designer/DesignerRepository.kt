package ro.chiralinteriordesign.cull.model.designer

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

class DesignerRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun getDesigners(): ResultWrapper<List<Designer>> {
        return safeApiCall { webservice.getDesigners() }
    }
}