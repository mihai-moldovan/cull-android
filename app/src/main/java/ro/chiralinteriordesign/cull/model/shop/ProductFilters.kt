package ro.chiralinteriordesign.cull.model.shop

import ro.chiralinteriordesign.cull.model.user.RoomType

/**
 * Created by Mihai Moldovan on 13/02/2021.
 */
data class ProductFilters(
    val query: String? = null,
    val productType: String? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val color: String? = null,
    val material: String? = null,
    val roomType: RoomType? = null,
    val quizResult: String? = null,
)