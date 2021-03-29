package ro.chiralinteriordesign.cull.ui.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.shop.Cart

class CartListViewModel : ViewModel() {

    private val dataRepo = App.instance.dataRepository
    private val shopRepo = dataRepo.productRepository

    val cartList = MutableLiveData(shopRepo.carts)

    fun deleteCart(cart: Cart) {
        shopRepo.removeCart(cart)
        cartList.postValue(shopRepo.carts)
    }
}