package krio.systemdesign.shoppingapp.shared.domain.model

sealed interface ItemIssue {
    val productId: String

    data class Unavailable(override val productId: String) : ItemIssue
    data class PriceChanged(
        override val productId: String,
        val newPrice: Long,
    ) : ItemIssue
    data class NotEnoughStock(
        override val productId: String,
        val availableQuantity: Int,
    ) : ItemIssue
}
