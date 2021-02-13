package ro.chiralinteriordesign.cull.model.shop

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.*
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.services.*
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

class ProductRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun loadProducts(
        page: Int = 1,
        filters: ProductFilters? = null,
    ): ResultWrapper<PaginatedResponse<Product>> {
        return safeApiCall {
            webservice.getProducts(
                page,
                filters?.query,
                filters?.productType,
                filters?.minPrice,
                filters?.maxPrice,
                filters?.color,
                filters?.material,
                filters?.roomType?.name,
                filters?.quizResult,
            )
        }.apply {
            if (this is ResultWrapper.Success) {
                val jobs = mutableListOf<Job>()
                for (p in this.value.results) {
                    p.thumbnails.firstOrNull()?.let { thumb ->
                        jobs.add(withContext(Dispatchers.IO) {
                            async {
                                try {
                                    Glide
                                        .with(App.instance)
                                        .load(thumb)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .submit()
                                        .get()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        })
                    }
                }
                jobs.joinAll()
            }
        }
    }
}