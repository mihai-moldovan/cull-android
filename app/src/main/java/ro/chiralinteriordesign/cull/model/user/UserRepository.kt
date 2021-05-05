package ro.chiralinteriordesign.cull.model.user

import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.services.LocalRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.Webservice
import ro.chiralinteriordesign.cull.services.safeApiCall
import java.io.Serializable


/**
 * Created by Mihai Moldovan on 21/12/2020.
 */

class UserRepository(
    private val localRepository: LocalRepository,
    private val webservice: Webservice
) {

    suspend fun login(email: String, password: String): ResultWrapper<User> {
        val response = safeApiCall { webservice.login(email, password) }
        when (response) {
            is ResultWrapper.Success -> {
                currentUser = response.value
            }
            else -> Unit
        }
        return response
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): ResultWrapper<User> {
        val response = safeApiCall { webservice.register(firstName, lastName, email, password) }
        when (response) {
            is ResultWrapper.Success -> {
                currentUser = response.value
            }
            else -> Unit
        }
        return response
    }

    suspend fun saveUserData(
        firstName: String? = null,
        lastName: String? = null,
        password: String? = null,
        quizResult: String? = null
    ): ResultWrapper<User> {
        val response =
            safeApiCall { webservice.saveUser(firstName, lastName, password, quizResult) }
        when (response) {
            is ResultWrapper.Success -> {
                currentUser = response.value
            }
            else -> Unit
        }
        return response
    }

    val isLoggedIn: Boolean get() = currentUser.id > 0

    var currentUser: User
        get() {
            if (localRepository[User::javaClass.name] !is User) {
                localRepository[User::javaClass.name] = User()
            }
            return localRepository[User::javaClass.name] as User
        }
        set(newValue) {
            localRepository[User::javaClass.name] = newValue
        }

    var room: Room?
        get() = localRepository[Room::class.java.name] as? Room
        set(newValue) {
            localRepository[Room::class.java.name] = newValue
        }

    fun logout() {
        currentUser = User()
        localRepository[Room::class.java.name] = null
        localRepository[Cart::class.java.name] = null
    }
}