package krio.systemdesign.shoppingapp.contract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// POST /cart/validate: the cart the app is about to check out.
@Serializable
data class CartValidationRequestDTO(
    val items: List<CartItemDTO>,
    val promoCode: String? = null,
)

@Serializable
data class CartItemDTO(
    val productId: String,
    // The price the user sees; an outdated one gets ItemIssueDTO.PriceChanged.
    val price: Long,
    val quantity: Int,
)

// The answer to POST /cart/validate. Mirrors CartValidationResult and ItemIssue in the app's :crossfeature:domain.
@Serializable
data class CartValidationResponseDTO(
    val issues: List<ItemIssueDTO>,
    // false when the sent code no longer exists; true when no code was sent.
    val promoCodeValid: Boolean,
)

// The kind goes in the "type" field: {"type": "priceChanged", "productId": "1", "newPrice": 8999}.
@Serializable
sealed interface ItemIssueDTO {
    @Serializable
    @SerialName("unavailable")
    data class Unavailable(val productId: String) : ItemIssueDTO

    @Serializable
    @SerialName("priceChanged")
    data class PriceChanged(
        val productId: String,
        val newPrice: Long,
    ) : ItemIssueDTO

    // availableQuantity is above 0: at 0 the item gets Unavailable instead.
    @Serializable
    @SerialName("notEnoughStock")
    data class NotEnoughStock(
        val productId: String,
        val availableQuantity: Int,
    ) : ItemIssueDTO
}
