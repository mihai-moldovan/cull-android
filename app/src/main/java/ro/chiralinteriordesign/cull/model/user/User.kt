package ro.chiralinteriordesign.cull.model.user

import android.content.Context
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.model.shop.Cart
import java.io.Serializable

/**
 * Created by Mihai Moldovan on 25/12/2020.
 */
data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val quizResult: String = "",
    val authToken: String? = null,
) : Serializable

enum class RoomType {
    LIVING, DINING, LOBBY, OFFICE, BEDROOM, CHILDROOM;

    fun stringName(context: Context) =
        context.getString(
            when (this) {
                LIVING -> R.string.space_room_type_living
                DINING -> R.string.space_room_type_dining
                LOBBY -> R.string.space_room_type_lobby
                OFFICE -> R.string.space_room_type_office
                BEDROOM -> R.string.space_room_type_bedroom
                CHILDROOM -> R.string.space_room_type_child_room
            }
        )
}


data class Room(
    val roomType: RoomType? = null,
    val area: Int = 0,
) : Serializable {
    val isValid: Boolean = roomType != null && area > 0
}