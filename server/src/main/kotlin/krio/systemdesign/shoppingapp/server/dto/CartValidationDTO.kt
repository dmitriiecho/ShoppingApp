package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Корзина, которую приложение присылает на проверку перед оформлением заказа.
// Формат совпадает с CartValidationDTO в приложении (:data).
@Serializable
data class CartValidationRequestDTO(
    val items: List<CartItemDTO>,
    val promoCode: String? = null,
)

@Serializable
data class CartItemDTO(
    val productId: String,
    // Цена, которую видит пользователь: если она устарела, сервер вернёт ItemIssueDTO.PriceChanged.
    val price: Long,
    val quantity: Int,
)

// Пустой список проблем — корзина в порядке. Соответствует CartValidationResult и ItemIssue в :domain.
@Serializable
data class CartValidationResponseDTO(
    val issues: List<ItemIssueDTO>,
)

// В JSON вид проблемы передаётся полем "type": {"type": "priceChanged", "productId": "1", "newPrice": 8999}.
@Serializable
sealed interface ItemIssueDTO {
    @Serializable
    @SerialName("unavailable")
    data class Unavailable(val productId: String) : ItemIssueDTO

    @Serializable
    @SerialName("priceChanged")
    data class PriceChanged(val productId: String, val newPrice: Long) : ItemIssueDTO
}
