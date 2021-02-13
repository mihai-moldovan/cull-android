package ro.chiralinteriordesign.cull.ui.products

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.shop.Product
import ro.chiralinteriordesign.cull.model.shop.ProductFilters
import ro.chiralinteriordesign.cull.services.ResultWrapper

class ProductsViewModel(app: Application) : AndroidViewModel(app) {

    private val dataRepo = App.instance.dataRepository
    private val userRepo = dataRepo.userRepository
    private val quizRepo = dataRepo.quizRepository

    val isLoading = MutableLiveData(false)
    val products = MutableLiveData(mutableListOf<Product>())
    val roomStyle = MutableLiveData("")

    private var currentPage = 0
    private var hasReachedEnd = false
    private var currentJob: Job? = null
    private var currentFilters: ProductFilters? = null
        set(newValue) {
            field = newValue
            resetProducts()
        }


    private fun resetProducts() {
        currentJob?.cancel()
        currentJob = null
        currentPage = 0
        hasReachedEnd = false
        products.postValue(mutableListOf())
    }

    fun loadNextPage(onEnd: (() -> Unit)? = null) {
        if (currentJob?.isActive != true) {
            currentJob = viewModelScope.launch(Dispatchers.IO) {
                if (hasReachedEnd) {
                    onEnd?.invoke()
                    return@launch
                }
                isLoading.postValue(true)
                currentPage += 1
                when (val result =
                    dataRepo.productRepository.loadProducts(currentPage, currentFilters)) {
                    is ResultWrapper.Success -> {
                        val pList = products.value ?: mutableListOf()
                        pList.addAll(result.value.results)
                        products.postValue(pList)
                    }
                    else -> {
                        hasReachedEnd = true
                    }
                }
                isLoading.postValue(false)
                onEnd?.invoke()
            }
        } else {
            onEnd?.invoke()
        }
    }

    fun searchQuery(query: String = "") {
        currentFilters = (currentFilters ?: ProductFilters()).copy(
            query = query,
            roomType = null,
            quizResult = null
        )
        this.roomStyle.postValue(null)
    }

    fun loadRoomAndStyle() {
        val user = userRepo.currentUser
        val quizResult = quizRepo.localQuiz?.results?.firstOrNull { it.key == user.quizResult }
        val roomType = user.rooms.firstOrNull()?.roomType
        currentFilters = (currentFilters ?: ProductFilters()).copy(
            query = null,
            roomType = roomType,
            quizResult = user.quizResult
        )
        this.roomStyle.postValue("${roomType?.stringName(getApplication()) ?: ""} ${quizResult?.title ?: ""}")
    }
}