package ro.chiralinteriordesign.cull.model.shop

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.annotations.Expose
import ro.chiralinteriordesign.cull.model.quiz.QuizResult
import ro.chiralinteriordesign.cull.model.user.RoomType
import java.io.Serializable
import java.util.*

/**
 * Created by Mihai Moldovan on 3/21/21.
 */
data class Cart(
    val id: Int,
    val lineItems: MutableList<CartLineItem>,
    val name: String,
    val roomType: RoomType,
    val style: String,
    var isSent: Boolean,
) : Serializable {


    val total: Float
        get() {
            return lineItems.foldRight(0.0f) { li, acc -> acc + li.variant.price * li.quantity }
        }

    val totalString: String
        get() {
            return currencyFormat.format(total)
        }

    val cartSerialization: Map<String, Any>
        get() = mapOf(
            "line_items" to lineItems.map { it.cartSerialization },
            "name" to name,
            "room_type" to roomType.name.toLowerCase(Locale.US),
            "style" to style,
            "is_sent" to isSent,
        )
}

data class CartLineItem(
    var quantity: Int,
    val variant: ProductVariant,
    val product: Product,
) : Serializable {


    val priceString: String
        get() {
            return currencyFormat.format(variant.price * quantity)
        }

    val cartSerialization: Map<String, Any>
        get() = mapOf(
            "quantity" to quantity,
            "variant" to variant.id
        )
}