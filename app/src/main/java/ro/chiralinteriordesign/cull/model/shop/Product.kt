package ro.chiralinteriordesign.cull.model.shop

import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.R
import java.io.Serializable
import java.text.NumberFormat
import java.util.*
import kotlin.math.max

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

val currencyFormat = NumberFormat.getCurrencyInstance().apply {
    maximumFractionDigits = 0
    currency = Currency.getInstance("RON")
}

private val sizeChange = "_${App.instance.resources.displayMetrics.widthPixels}x"
fun transformToThumbnail(img: String): String {
    return img.replace("/(.*)(\\.(?:jp|png).*)/".toRegex()) {
        "${it.groupValues[1]}${sizeChange}${it.groupValues[2]}"
    }
}

data class Product(
    val id: Long,
    val supplier: String,
    val title: String,
    val bodyHtml: String,
    val minPrice: Float,
    val maxPrice: Float,
    val images: List<String>,
    val variants: List<ProductVariant>,
    val tags: List<String>,
    val category: String
) : Serializable {


    val priceString: String
        get() = currencyFormat.format((minPrice + maxPrice) / 2)

    private lateinit var _thumbnails: List<String>
    val thumbnails: List<String>
        get() {
            if (!::_thumbnails.isInitialized) {
                _thumbnails = this.images.map { transformToThumbnail(it) }
            }
            return _thumbnails
        }
}


data class ProductVariant(
    val id: Long,
    val title: String,
    val price: Float,
    val image: String?
) : Serializable {

    val priceString: String
        get() = currencyFormat.format(price)

    private var _thumbnail: String? = null
    val thumbnail: String?
        get() {
            if (_thumbnail == null && image != null) {
                _thumbnail = transformToThumbnail(image)
            }
            return _thumbnail

        }
}

data class ProductFilters(
    val query: String? = null,
    val productType: String? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val color: String? = null,
    val material: String? = null,
    val moodboardId: Int? = null,
)
