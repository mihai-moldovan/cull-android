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
    val roomLength = MutableLiveData(0)
    val roomWidth = MutableLiveData(0)
    val roomHeight = MutableLiveData(0)
    val roomWindows = MutableLiveData(0)
    val roomDoors = MutableLiveData(0)

    val room = MediatorLiveData<Room>().apply {
        value = Room()
        addSource(roomType) { postValue(this.value?.copy(roomType = it)) }
        addSource(roomLength) { postValue(this.value?.copy(length = it)) }
        addSource(roomWidth) { postValue(this.value?.copy(width = it)) }
        addSource(roomHeight) { postValue(this.value?.copy(height = it)) }
        addSource(roomWindows) { postValue(this.value?.copy(windows = it)) }
        addSource(roomDoors) { postValue(this.value?.copy(doors = it)) }
    }

    fun saveRoom() {
        val room = room.value ?: return
        if (!room.isValid) {
            return
        }
        userRepository.currentUser = userRepository.currentUser.copy(rooms = listOf(room))
    }

}