package ro.chiralinteriordesign.cull.model.text

import java.io.Serializable

/**
 * Created by Mihai Moldovan on 31/12/2020.
 */
data class Text(
    val key: String,
    val value: String,
) : Serializable {
    enum class Key(val key: String) {
        TERMS("terms")
    }
}