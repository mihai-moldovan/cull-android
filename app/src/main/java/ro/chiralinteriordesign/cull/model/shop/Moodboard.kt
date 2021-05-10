package ro.chiralinteriordesign.cull.model.shop

import ro.chiralinteriordesign.cull.model.user.RoomType

/**
 * Created by Mihai Moldovan on 13/02/2021.
 */

data class Moodboard(
    val id: Int,
    val name: String? = null,
)

data class MoodBoardFilters(
    val roomType: RoomType,
    val quizResult: String,
    val roomArea: Int? = null,
    val lastMoodboardId: Int? = null,
)