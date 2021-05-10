package ro.chiralinteriordesign.cull.model.designer

/**
 * Created by Mihai Moldovan on 3/28/21.
 */
data class Designer(
    val id: Long,
    val name: String,
    val description: String,
    val photo: String?,
    val position: Int,
    val email: String?,
    val phone: String?,
)