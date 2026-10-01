package krio.systemdesign.shoppingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.ItemIssue

// Формат совпадает с CartValidationDTO на сервере (server/src/main/kotlin/.../dto/CartValidationDTO.kt).
@Serializable
internal data class CartValidationRequestDTO(
    val items: List<CartItemDTO>,
    val promoCode: String? = null,
)

@Serializable
internal data class CartItemDTO(
    val productId: String,
    // Цена, которую видит пользователь: если она устарела, сервер вернёт ItemIssueDTO.PriceChanged.
    val price: Long,
    val quantity: Int,
)

// Корзина в порядке, если список проблем с товарами пуст и промокод действует.
@Serializable
internal data class CartValidationResponseDTO(
    val issues: List<ItemIssueDTO>,
    // false — присланного промокода больше нет. Если промокод не прислан, true.
    val promoCodeValid: Boolean,
)

// Вид проблемы сервер передаёт полем "type": {"type": "priceChanged", "productId": "1", "newPrice": 8999}.
@Serializable
internal sealed interface ItemIssueDTO {
    @Serializable
    @SerialName("unavailable")
    data class Unavailable(val productId: String) : ItemIssueDTO

    @Serializable
    @SerialName("priceChanged")
    data class PriceChanged(val productId: String, val newPrice: Long) : ItemIssueDTO

    @Serializable
    @SerialName("notEnoughStock")
    data class NotEnoughStock(val productId: String, val availableQuantity: Int) : ItemIssueDTO
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
