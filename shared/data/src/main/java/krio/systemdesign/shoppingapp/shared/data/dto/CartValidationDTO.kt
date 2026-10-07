// Works around a false IDE error on @Serializable classes; the Gradle build doesn't need it.
@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package krio.systemdesign.shoppingapp.shared.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue

// Same format as CartValidationDTO.kt in server/.
@Serializable
internal data class CartValidationRequestDTO(
    val items: List<CartItemDTO>,
    val promoCode: String? = null,
)

@Serializable
internal data class CartItemDTO(
    val productId: String,
    val price: Long,
    val quantity: Int,
)

@Serializable
internal data class CartValidationResponseDTO(
    val issues: List<ItemIssueDTO>,
    val promoCodeValid: Boolean,
)

@Serializable
internal sealed interface ItemIssueDTO {
    @Serializable
    @SerialName("unavailable")
    data class Unavailable(val productId: String) : ItemIssueDTO

    @Serializable
    @SerialName("priceChanged")
    data class PriceChanged(
        val productId: String,
        val newPrice: Long,
    ) : ItemIssueDTO

    @Serializable
    @SerialName("notEnoughStock")
    data class NotEnoughStock(
        val productId: String,
        val availableQuantity: Int,
    ) : ItemIssueDTO
}

internal fun Cart.toValidationRequest(): CartValidationRequestDTO = CartValidationRequestDTO(
    items = items.map { CartItemDTO(productId = it.productId, price = it.price, quantity = it.quantity) },
    promoCode = promoCode?.code,
)

internal fun ItemIssueDTO.toDomain(): ItemIssue = when (this) {
    is ItemIssueDTO.Unavailable -> ItemIssue.Unavailable(productId)
    is ItemIssueDTO.PriceChanged -> ItemIssue.PriceChanged(productId, newPrice)
    is ItemIssueDTO.NotEnoughStock -> ItemIssue.NotEnoughStock(productId, availableQuantity)
}
