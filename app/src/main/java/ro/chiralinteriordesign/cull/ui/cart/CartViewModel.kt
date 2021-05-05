package ro.chiralinteriordesign.cull.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.Product

/**
 * Created by Mihai Moldovan on 3/26/21.
 */
class CartViewModel : ViewModel() {

    private val dataRepo = App.instance.dataRepository
    private val userRepo = dataRepo.userRepository
    private val shopRepo = dataRepo.productRepository

    val cart = MutableLiveData<Cart>()
    var cartIndex = 0

    fun addProduct(product: Product) {
        cart.value?.let {
            shopRepo.addProductInCart(it, product)
            cart.postValue(it)
        }
    }

    fun removeProduct(product: Product, all: Boolean) {
        cart.value?.let {
            shopRepo.removeProductFromCart(it, product, all)
            cart.postValue(it)
        }
    }

    fun requestOffer(): LiveData<Boolean> {
        return cart.value?.let { cart ->
            liveData(Dispatchers.IO) {
                val result = shopRepo.requestCartOffer(cart)
                this@CartViewModel.cart.postValue(cart)
                emit(result)
            }
        } ?: MutableLiveData(false)

    }

    val isLoggedIn: Boolean
        get() = userRepo.isLoggedIn
}