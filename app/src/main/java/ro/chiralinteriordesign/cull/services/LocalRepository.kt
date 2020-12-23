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

    companion object {

        const val TAG = "LocalRepository"

        private const val FILE_QUIZ = "quiz.bin"

        @JvmField
        val QUIZ_ACTION = LocalRepository::javaClass.name + "/quiz_update"
    }

    var quiz: Quiz? by RepositoryDelegate(FILE_QUIZ, QUIZ_ACTION)

    inner class RepositoryDelegate<T : Serializable>(
        private val fileName: String,
        private val updateAction: String? = null
    ) :
        ReadWriteProperty<LocalRepository, T?> {
        private var wasLoaded = false
        private var _value: T? = null

        override fun getValue(thisRef: LocalRepository, property: KProperty<*>): T? {
            if (!wasLoaded) {
                wasLoaded = true
                @Suppress("UNCHECKED_CAST")
                _value = load(fileName) as? T
            }
            return _value
        }

        override fun setValue(thisRef: LocalRepository, property: KProperty<*>, value: T?) {
            _value = value
            wasLoaded = true
            save(value, fileName)
            updateAction?.let {
                LocalBroadcastManager.getInstance(appContext)
                    .sendBroadcast(
                        Intent(it)
                    )
            }
        }
    }

    private fun load(fileName: String): Serializable? {
        var ois: ObjectInputStream? = null
        val f = File(appContext.filesDir, fileName)
        try {
            ois = ObjectInputStream(FileInputStream(f))
            return ois.readObject() as? Serializable
        } catch (e: Exception) {
            Log.w(TAG, "problem reading file: $fileName")
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