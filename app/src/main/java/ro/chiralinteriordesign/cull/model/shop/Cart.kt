package ro.chiralinteriordesign.cull.model.shop

import ro.chiralinteriordesign.cull.model.quiz.QuizResult
import ro.chiralinteriordesign.cull.model.user.RoomType
import java.io.Serializable

/**
 * Created by Mihai Moldovan on 3/21/21.
 */
data class Cart(
    val id: Int,
    val lineItems: MutableList<CartLineItem>,
    val name: String,
    val roomType: RoomType,
    val style: String,
) : Serializable {


    val total: Float
        get() = lineItems.foldRight(0.0f) { li, acc -> acc + li.variant.price * li.quantity }

    val totalString: String
        get() = currencyFormat.format(total)

}

data class CartLineItem(
    val quantity: Int,
    val variant: ProductVariant,
    val product: Product,
) : Serializable