package ro.chiralinteriordesign.cull.ui.designers

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.designer.ContactDesigners
import ro.chiralinteriordesign.cull.model.designer.Designer
import ro.chiralinteriordesign.cull.services.ResultWrapper
import java.util.*

class DesignersListViewModel : ViewModel() {
    private val dataRepo = App.instance.dataRepository
    private val designersRepo = dataRepo.designersRepository
    private val userRepo = dataRepo.userRepository
    private val shopRepo = dataRepo.productRepository

    val designers = MutableLiveData(listOf<Designer>())
    val isLoading = MutableLiveData(false)

    fun loadDesigners() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            when (val r = designersRepo.getDesigners()) {
                is ResultWrapper.Success -> {
                    designers.postValue(r.value)

                }
            }
            isLoading.postValue(false)
        }
    }

    fun contactDesigners(list: List<Long>): LiveData<Boolean> {
        val user = userRepo.currentUser
        val room = userRepo.room
        val roomType = room?.roomType ?: return MutableLiveData(false)
        isLoading.postValue(true)
        return liveData(Dispatchers.IO) {
            emit(
                designersRepo.contactDesigners(
                    ContactDesigners(
                        list,
                        roomType.name.toLowerCase(Locale.US),
                        user.quizResult,
                        shopRepo.getOrAddCart(roomType, user.quizResult, "").lineItems.map { it.product.id }
                    )
                )
            )
            isLoading.postValue(false)
        }
    }
}