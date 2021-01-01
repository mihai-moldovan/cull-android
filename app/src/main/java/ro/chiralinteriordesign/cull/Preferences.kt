package ro.chiralinteriordesign.cull

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Mihai Moldovan on 20/12/2020.
 */
class Preferences(app: Application) {

    enum class Key {
        TUTORIAL_SEEN,
        CACHE_TIMESTAMP,
        LAST_VERSION,
    }

    private val mSharedPreferences: SharedPreferences

    @Synchronized
    fun getLong(key: Key, defValue: Long): Long {
        return mSharedPreferences.getLong(key.name, defValue)
    }

    @Synchronized
    fun put(key: Key, value: Long) {
        mSharedPreferences.edit().putLong(key.name, value).apply()
    }

    @Synchronized
    fun clear(key: Key) {
        mSharedPreferences.edit().remove(key.name).apply()
    }

    @Synchronized
    fun getInt(key: Key, defValue: Int): Int {
        return mSharedPreferences.getInt(key.name, defValue)
    }

    @Synchronized
    fun put(key: Key, value: Int) {
        mSharedPreferences.edit().putInt(key.name, value).apply()
    }

    @Synchronized
    fun getString(key: Key): String? {
        return mSharedPreferences.getString(key.name, null)
    }

    @Synchronized
    fun put(key: Key, value: String?) {
        mSharedPreferences.edit().putString(key.name, value).apply()
    }

    @Synchronized
    fun getBoolean(key: Key, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key.name, defValue)
    }

    @Synchronized
    fun put(key: Key, value: Boolean) {
        mSharedPreferences.edit().putBoolean(key.name, value).apply()
    }

    companion object {
        private const val GENERAL_PREFERENCES = "general_preferences"
        val instance: Preferences
            get() = App.instance.preferences
    }

    init {
        mSharedPreferences = app.getSharedPreferences(
            GENERAL_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }
}