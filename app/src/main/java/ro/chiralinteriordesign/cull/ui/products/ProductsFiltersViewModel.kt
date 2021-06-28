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

class ProductsFiltersViewModel(app: Application) : AndroidViewModel(app) {

    private val dataRepo = App.instance.dataRepository
    private val shopRepo = dataRepo.productRepository

    val isLoading = MutableLiveData(false)
    val filtersData = MutableLiveData<FiltersData?>()


    fun loadFilters() {
        viewModelScope.launch {
            val filters = shopRepo.getFilters()
            when (filters) {
                is ResultWrapper.Success -> {
                    filtersData.postValue(filters.value)
                }
            }
        }
    }
}