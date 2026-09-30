package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CartUiState(
    val items: ImmutableList<CartItem> = persistentListOf(),
    val subtotal: Long = 0,
    val discount: Long = 0,
    val totalPrice: Long = 0,
    val promoCode: PromoCode? = null,
    val issues: ImmutableList<ItemIssue> = persistentListOf(),
    val isValidating: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    val isEmpty: Boolean get() = items.isEmpty()
}
