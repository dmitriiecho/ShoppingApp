package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CartUiState(
    val items: ImmutableList<CartItem> = persistentListOf(),
    val totalPrice: Long = 0,
    val appliedPromoCode: String? = null,
    val showPromoSuccess: Boolean = false,
    val issues: ImmutableList<ItemIssue> = persistentListOf(),
    val isValidating: Boolean = false,
) {
    val isEmpty: Boolean get() = items.isEmpty()
}
