package krio.systemdesign.shoppingapp.domain.model

sealed interface ItemIssue {
    data class Unavailable(val productId: String) : ItemIssue
    data class PriceChanged(val productId: String, val newPrice: Long) : ItemIssue
}
