package ro.chiralinteriordesign.cull.model.user

import java.io.Serializable

/**
 * Created by Mihai Moldovan on 25/12/2020.
 */
data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val quizResultId: Int = 0,
    val authToken: String? = null,
    val rooms: List<Room> = listOf(),
) : Serializable

enum class RoomType { LIVING, DINING, LOBBY, OFFICE, BEDROOM, CHILDROOM }

data class Room(
    val roomType: RoomType? = null,
    val length: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val windows: Int = 0,
    val doors: Int = 0,
) : Serializable {


    val isValid: Boolean = roomType != null &&
            length > 0 &&
            width > 0 &&
            height > 0 &&
            windows > 0 &&
            doors > 0

}