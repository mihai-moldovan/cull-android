package ro.chiralinteriordesign.cull.ui.products

import android.app.Application
import androidx.lifecycle.*
import androidx.work.ListenableWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.quiz.QuizResult
import ro.chiralinteriordesign.cull.model.shop.*
import ro.chiralinteriordesign.cull.model.user.RoomType
import ro.chiralinteriordesign.cull.services.ResultWrapper

class ProductsViewModel(app: Application) : AndroidViewModel(app) {

    private val dataRepo = App.instance.dataRepository
    private val userRepo = dataRepo.userRepository
    private val quizRepo = dataRepo.quizRepository
    private val shopRepo = dataRepo.productRepository

    val isLoading = MutableLiveData(false)
    val products = MutableLiveData(mutableListOf<Product>())
    val roomStyle = MutableLiveData("")
    val query = MutableLiveData("")

    private lateinit var currentCart: Cart
    val currentCartIndex: Int? get() = shopRepo.getCartIndex(currentCart)


    private var currentPage = 0
    private var hasReachedEnd = false
    private var currentJob: Job? = null

    var currentFilters: ProductFilters? = null
        private set
    private var moodboardFilters: MoodBoardFilters? = null

    var isSearchScreen = false
        private set

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

                val moodboardFilters = moodboardFilters
                if (moodboardFilters != null) {
                    when (val result = shopRepo.getMoodboard(moodboardFilters)) {
                        is ResultWrapper.Success -> {
                            currentFilters = ProductFilters(moodboardId = result.value.id)
                            this@ProductsViewModel.moodboardFilters = null
                        }
                        else -> hasReachedEnd = true
                    }
                }

                if (!hasReachedEnd) {
                    currentPage += 1
                    when (val result =
                        shopRepo.loadProducts(currentPage, currentFilters)) {
                        is ResultWrapper.Success -> {
                            val pList = products.value ?: mutableListOf()
                            pList.addAll(result.value)
                            products.postValue(pList)
                        }
                        else -> {
                            hasReachedEnd = true
                        }
                    }
                }
                isLoading.postValue(false)
                onEnd?.invoke()
            }
        } else {
            onEnd?.invoke()
        }
    }

    fun searchQuery(filters: ProductFilters, cartIndex: Int) {
        isSearchScreen = true
        currentFilters = filters
        moodboardFilters = null
        resetProducts()
        this.roomStyle.postValue(null)
        this.query.postValue(filters.query)
        currentCart = shopRepo.carts!![cartIndex]
    }

    fun loadRoomAndStyle() {
        isSearchScreen = false
        val user = userRepo.currentUser
        val room = userRepo.room
        val quizResult = quizRepo.localQuiz?.results?.firstOrNull { it.key == user.quizResult } ?: return
        val roomType = room?.roomType ?: return

        moodboardFilters = MoodBoardFilters(
            roomType = roomType,
            roomArea = room.area,
            quizResult = quizResult.key,
            lastMoodboardId = currentFilters?.moodboardId
        )
        currentFilters = null
        resetProducts()

        val name = "${roomType.stringName(getApplication()) ?: ""} ${quizResult.title ?: ""}"
        this.roomStyle.postValue(name)
        this.query.postValue(null)
        currentCart = shopRepo.getOrAddCart(roomType, user.quizResult, name)
    }

    val isFiltered: Boolean
        get() = currentFilters?.isFiltered ?: false

}