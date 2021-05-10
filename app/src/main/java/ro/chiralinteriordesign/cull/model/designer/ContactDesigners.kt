package ro.chiralinteriordesign.cull.model.designer

import java.io.Serializable

/**
 * Created by Mihai Moldovan on 10.05.2021.
 */
data class ContactDesigners(
    val designers: List<Long>,
    val roomType: String,
    val style: String,
    val products: List<Long>
) : Serializable