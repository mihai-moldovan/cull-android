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
) : Serializable