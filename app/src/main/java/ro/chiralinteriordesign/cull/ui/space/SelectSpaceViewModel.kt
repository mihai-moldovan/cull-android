package ro.chiralinteriordesign.cull.ui.space

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.model.user.Room
import ro.chiralinteriordesign.cull.model.user.RoomType
import ro.chiralinteriordesign.cull.model.user.UserRepository

/**
 * Created by Mihai Moldovan on 26/12/2020.
 */
class SelectSpaceViewModel : ViewModel() {

    private val userRepository by lazy { App.instance.dataRepository.userRepository }

    val roomType = MutableLiveData<RoomType>()
    val roomArea = MutableLiveData(0)

    val room = MediatorLiveData<Room>().apply {
        value = Room()
        addSource(roomType) { postValue(this.value?.copy(roomType = it)) }
        addSource(roomArea) { postValue(this.value?.copy(area = it)) }
    }

    fun saveRoom() {
        val room = room.value ?: return
        if (!room.isValid) {
            return
        }
        userRepository.room = room
    }

}