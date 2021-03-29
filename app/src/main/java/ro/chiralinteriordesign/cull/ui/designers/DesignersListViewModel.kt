package ro.chiralinteriordesign.cull.ui.designers

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.designer.Designer
import ro.chiralinteriordesign.cull.services.ResultWrapper

class DesignersListViewModel : ViewModel() {
    private val dataRepo = App.instance.dataRepository
    private val designersRepo = dataRepo.designersRepository

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
}