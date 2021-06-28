package ro.chiralinteriordesign.cull.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ro.chiralinteriordesign.cull.model.quiz.Quiz
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Mihai Moldovan on 21/12/2020.
 */
class LocalRepository(val appContext: Context) {

    private val data = hashMapOf<String, Serializable>()

    operator fun get(key: String): Serializable? {
        return data[key] ?: load(key).also { it?.let { data[key] = it } }
    }

    operator fun set(key: String, value: Serializable?) {
        if (value != null) {
            data[key] = value
        } else {
            data.remove(key)
        }
        save(value, key)
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(Intent(intentAction(key)))
    }

    fun intentAction(key: String): String {
        return LocalRepository::class.java.name + "/" + key
    }


    private fun load(fileName: String): Serializable? {
        var ois: ObjectInputStream? = null
        val f = File(appContext.filesDir, fileName)
        try {
            ois = ObjectInputStream(FileInputStream(f))
            return ois.readObject() as? Serializable
        } catch (e: Exception) {
            f.delete()
        } finally {
            ois?.close()
        }
        return null
    }

    private fun save(obj: Serializable?, fileName: String): Boolean {
        try {
            val f = File(appContext.filesDir, fileName)
            if (obj == null) {
                f.delete()
            } else {
                val oos = ObjectOutputStream(FileOutputStream(f))
                oos.writeObject(obj)
                oos.close()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}