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
    // У одного товара их может быть несколько, например новая цена и нехватка остатка.
    val itemIssues: ImmutableMap<String, ImmutableList<ItemIssue>> = persistentMapOf(),
    // Сколько штук можно заказать по последней проверке, если она сообщила об остатке товара. Ключ — productId.
    val stockLimits: ImmutableMap<String, Int> = persistentMapOf(),
    // false — проверка нашла, что применённый промокод больше не действует, и его ещё не убрали.
    val isPromoCodeValid: Boolean = true,
    val isValidating: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
    // Корзина ещё не прочитана из базы. Пустой список здесь не значит, что корзина пуста.
    val isLoading: Boolean = false,
) {
    val isEmpty: Boolean get() = items.isEmpty()

    val priceChangeCount: Int get() = itemIssues.values.sumOf { issues -> issues.count { it is ItemIssue.PriceChanged } }

    val unavailableItemCount: Int get() = itemIssues.values.sumOf { issues -> issues.count { it is ItemIssue.Unavailable } }

    val notEnoughStockItemCount: Int get() = itemIssues.values.sumOf { issues -> issues.count { it is ItemIssue.NotEnoughStock } }

    // Оформить заказ можно, только когда все найденные проверкой изменения исправлены.
    val canCheckout: Boolean get() = itemIssues.isEmpty() && isPromoCodeValid

    // «+» работает, пока в корзине меньше остатка: того, что сообщила проверка,
    // а если она про этот товар ничего не сказала — запомненного при добавлении из каталога.
    fun canIncrease(item: CartItem): Boolean =
        item.quantity < (stockLimits[item.productId] ?: item.availableQuantity)
}
