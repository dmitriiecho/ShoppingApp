package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

data class CartUiState(
    val items: ImmutableList<CartItem> = persistentListOf(),
    val subtotal: Long = 0,
    val discount: Long = 0,
    val totalPrice: Long = 0,
    val promoCode: PromoCode? = null,
    // Изменения в товарах, которые нашла проверка корзины и которые ещё не исправлены. Ключ — productId.
    val itemIssues: ImmutableMap<String, ItemIssue> = persistentMapOf(),
    // false — проверка нашла, что применённый промокод больше не действует, и его ещё не убрали.
    val isPromoCodeValid: Boolean = true,
    val isValidating: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    val isEmpty: Boolean get() = items.isEmpty()

    val priceChangeCount: Int get() = itemIssues.values.count { it is ItemIssue.PriceChanged }

    val unavailableItemCount: Int get() = itemIssues.values.count { it is ItemIssue.Unavailable }

    // Оформить заказ можно, только когда все найденные проверкой изменения исправлены.
    val canCheckout: Boolean get() = itemIssues.isEmpty() && isPromoCodeValid
}
