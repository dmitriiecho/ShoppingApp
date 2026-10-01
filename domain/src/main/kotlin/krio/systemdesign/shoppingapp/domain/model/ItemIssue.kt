package krio.systemdesign.shoppingapp.domain.model

sealed interface ItemIssue {
    data class Unavailable(val productId: String) : ItemIssue
    data class PriceChanged(val productId: String, val newPrice: Long) : ItemIssue
    // В корзине больше, чем можно заказать. availableQuantity больше 0: при 0 приходит Unavailable.
    data class NotEnoughStock(val productId: String, val availableQuantity: Int) : ItemIssue
}
