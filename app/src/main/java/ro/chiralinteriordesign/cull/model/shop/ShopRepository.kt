package ro.chiralinteriordesign.cull.model.shop

import com.google.gson.Gson
import okhttp3.RequestBody
import ro.chiralinteriordesign.cull.model.quiz.Quiz
import ro.chiralinteriordesign.cull.model.user.RoomType
import ro.chiralinteriordesign.cull.services.*
import java.io.Serializable
import java.util.*
import java.util.logging.Filter


/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

class ShopRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun loadProducts(
        page: Int = 1,
        filters: ProductFilters? = null,
    ): ResultWrapper<List<Product>> {
        return safeApiCall {
            webservice.getProducts(
                page,
                filters?.query,
                filters?.productType,
                filters?.minPrice,
                filters?.maxPrice,
                filters?.color,
                filters?.material,
                filters?.moodboardId,
            ).results
        }
    }

    suspend fun getMoodboard(
        filters: MoodBoardFilters
    ): ResultWrapper<Moodboard> {
        return safeApiCall {
            webservice.getMoodboard(
                filters.roomType.name.toLowerCase(Locale.US),
                filters.quizResult,
                filters.roomArea,
                filters.lastMoodboardId
            )
        }
    }

    suspend fun loadCarts(forceDownload: Boolean): ResultWrapper<List<Cart>> {
        val localData = localRepository[this.javaClass.name] as? List<Cart>
        return if (localData != null && !forceDownload) {
            ResultWrapper.Success(localData, true)
        } else {
            val response = safeApiCall { webservice.getCarts() }
            when (response) {
                is ResultWrapper.Success -> {
                    localRepository[this.javaClass.name] = response.value as? Serializable
                }
                else -> Unit
            }
            response
        }
    }

    var carts: List<Cart>?
        get() = localRepository[Cart::class.java.name] as? List<Cart>
        private set(newValue) {
            localRepository[Cart::class.java.name] = newValue as? Serializable
        }

    fun hasProductInCart(cart: Cart, product: Product): Boolean {
        return cart.lineItems.firstOrNull { it.product.id == product.id } != null
    }

    fun addProductInCart(cart: Cart, product: Product) {
        if (cart.isSent) {
            return
        }
        val index = cart.lineItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = cart.lineItems[index]
            cart.lineItems[index] = item.copy(quantity = item.quantity + 1)
        } else {
            cart.lineItems.add(CartLineItem(1, product.variants.first(), product))
        }
        saveCart(cart)
    }

    fun removeProductFromCart(cart: Cart, product: Product, all: Boolean = false) {
        if (cart.isSent) {
            return
        }
        val index = cart.lineItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = cart.lineItems[index]
            if (item.quantity > 1 && !all) {
                cart.lineItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                cart.lineItems.removeAt(index)
            }
            saveCart(cart)
        }
    }

    fun removeCart(cart: Cart) {
        carts = carts?.toMutableList()?.apply { remove(cart) }
    }

    fun addCart(cart: Cart) {
        carts = carts?.toMutableList()?.apply { add(cart) } ?: listOf(cart)
    }

    fun getOrAddCart(roomType: RoomType, style: String, name: String): Cart {
        return carts?.firstOrNull { it.roomType == roomType && it.style == style && !it.isSent } ?: Cart(
            0,
            mutableListOf(),
            name,
            roomType,
            style,
            false
        ).also { this.addCart(it) }
    }

    fun getCartIndex(cart: Cart): Int? = carts?.indexOf(cart)


    private fun saveCart(cart: Cart) {
        carts = if (carts?.contains(cart) == true) {
            carts
        } else {
            carts?.toMutableList()?.apply { add(cart) } ?: listOf(cart)
        }
    }


    suspend fun requestCartOffer(cart: Cart): Boolean {
        val response = safeApiCall {
            if (cart.id > 0) {
                webservice.sendExistingCart(cart.id)
            } else {
                val body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(cart.cartSerialization)
                )
                webservice.sendNewCart(body)
            }
        }
        return when (response) {
            is ResultWrapper.Success -> {
                cart.isSent = true
                saveCart(cart)
                true
            }
            else -> false
        }
    }

    suspend fun getFilters(): ResultWrapper<FiltersData> {
        val localData = localRepository[FiltersData::class.java.name] as? FiltersData
        return if (localData != null) {
            ResultWrapper.Success(localData, true)
        } else {
            val response = safeApiCall { webservice.getFilters() }
            when (response) {
                is ResultWrapper.Success -> {
                    localRepository[FiltersData::class.java.name] = response.value
                }
                else -> Unit
            }
            response
        }
    }
}