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

// Корзина в порядке, если список проблем с товарами пуст и промокод действует.
// Соответствует CartValidationResult и ItemIssue в :domain.
@Serializable
data class CartValidationResponseDTO(
    val issues: List<ItemIssueDTO>,
    // false — присланного промокода больше нет. Если промокод не прислан, true.
    val promoCodeValid: Boolean,
)

// В JSON вид проблемы передаётся полем "type": {"type": "priceChanged", "productId": "1", "newPrice": 8999}.
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

    // В корзине больше, чем можно заказать. availableQuantity больше 0: при 0 приходит Unavailable.
    @Serializable
    @SerialName("notEnoughStock")
    data class NotEnoughStock(
        val productId: String,
        val availableQuantity: Int,
    ) : ItemIssueDTO
}
