package ro.chiralinteriordesign.cull.ui.products

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.Product

/**
 * Created by Mihai Moldovan on 3/26/21.
 */
class ProductDetailsViewModel : ViewModel() {

    private val dataRepo = App.instance.dataRepository
    private val shopRepo = dataRepo.productRepository

    var currentCart: Cart? = null
        private set
    var currentCartIndex: Int? = null
        private set

    val product = MutableLiveData<Product?>(null)
    val isAdded = MediatorLiveData<Boolean>().apply {
        addSource(product) {
            postValue(it?.let { product ->
                currentCart?.lineItems?.firstOrNull { it.product.id == product.id } != null
            } ?: false)
        }
    }


    fun addRemoveProduct() {
        val product = product.value ?: return
        val cart = currentCart ?: return
        if (!shopRepo.hasProductInCart(cart, product)) {
            shopRepo.addProductInCart(cart, product)
            isAdded.postValue(true)
        } else {
            shopRepo.removeProductFromCart(cart, product, true)
            isAdded.postValue(false)
        }
    }

    fun setCartIndex(index: Int?) {
        shopRepo.carts?.let { carts ->
            val cartIndex = index ?: carts.size - 1
            currentCartIndex = cartIndex
            currentCart = carts[cartIndex]
        }
    }
}