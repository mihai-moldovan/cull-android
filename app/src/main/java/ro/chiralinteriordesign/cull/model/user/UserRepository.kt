package ro.chiralinteriordesign.cull.model.user

import ro.chiralinteriordesign.cull.services.LocalRepository
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.services.Webservice
import ro.chiralinteriordesign.cull.services.safeApiCall


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
                localRepository[this.javaClass.name] = response.value
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
                localRepository[this.javaClass.name] = response.value
            }
            else -> Unit
        }
        return response
    }

    suspend fun saveUserData(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        password: String? = null,
        quizResultId: Int? = null
    ): ResultWrapper<User> {
        val response =
            safeApiCall { webservice.save(firstName, lastName, email, password, quizResultId) }
        when (response) {
            is ResultWrapper.Success -> {
                localRepository[this.javaClass.name] = response.value
            }
            else -> Unit
        }
        return response
    }

    val isLoggedIn: Boolean get() = currentUser.id > 0

    var currentUser: User
        get() {
            if (localRepository[this.javaClass.name] !is User) {
                localRepository[this.javaClass.name] = User()
            }
            return localRepository[this.javaClass.name] as User
        }
        set(newValue) {
            localRepository[this.javaClass.name] = newValue
        }

    fun logout() {
        localRepository[this.javaClass.name] = User()
    }
}