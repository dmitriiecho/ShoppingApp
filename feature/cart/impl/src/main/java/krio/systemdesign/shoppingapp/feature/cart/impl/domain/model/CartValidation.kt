package krio.systemdesign.shoppingapp.feature.cart.impl.domain.model

import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartItem
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue

// What the cart's last validation found, kept until the next one. Unlike CartValidationResult, the server's
// answer to one request, this is what the app remembers from it. The server reports the changes; whether
// the user has fixed them since is decided here, against the current cart, so a change fixed in any way
// stops counting by itself.
internal data class CartValidation(
    val issues: List<ItemIssue> = emptyList(),
    // The code itself, not a flag: if another code is applied later, the finding doesn't concern it.
    val invalidPromoCode: String? = null,
) {
    // Changes the user hasn't fixed yet.
    fun pendingIssues(cart: Cart): List<ItemIssue> = issues.filter { it.isPendingIn(cart) }

    fun isPromoCodeValid(cart: Cart): Boolean = invalidPromoCode == null || cart.promoCode?.code != invalidPromoCode

    // An invalid promo code gives no discount, though it stays in the cart until the user removes it.
    fun discount(cart: Cart): Long = if (isPromoCodeValid(cart)) cart.discount() else 0

    fun totalPrice(cart: Cart): Long = cart.subtotal() - discount(cart)

    // The stock "+" may reach: the one the validation reported, else the one saved when the item was added.
    // Taken from every issue found, not only pending ones: once the user lowers the quantity to the stock,
    // "+" must not let them exceed it again.
    fun stockOf(item: CartItem): Int = issues.firstNotNullOfOrNull { issue ->
        when {
            issue.productId != item.productId -> null
            issue is ItemIssue.Unavailable -> 0
            issue is ItemIssue.NotEnoughStock -> issue.availableQuantity
            else -> null
        }
    } ?: item.availableQuantity

    // An order can be placed from a non-empty cart once every change is fixed and the promo code works.
    fun allowsCheckout(cart: Cart): Boolean =
        cart.items.isNotEmpty() && pendingIssues(cart).isEmpty() && isPromoCodeValid(cart)
}

// What to remember from the server's answer; null on a network error, so the last validation stays.
internal fun CartValidationResult.toCartValidation(cart: Cart): CartValidation? = when (this) {
    CartValidationResult.Success -> CartValidation()
    is CartValidationResult.Invalid -> CartValidation(
        issues = issues,
        invalidPromoCode = cart.promoCode?.code.takeUnless { isPromoCodeValid },
    )
    is CartValidationResult.Error -> null
}

// For a new price the item still has the old one; for missing stock, still more than can be ordered.
private fun ItemIssue.isPendingIn(cart: Cart): Boolean {
    val item = cart.items.firstOrNull { it.productId == productId } ?: return false
    return when (this) {
        is ItemIssue.Unavailable -> true
        is ItemIssue.PriceChanged -> item.price != newPrice
        is ItemIssue.NotEnoughStock -> item.quantity > availableQuantity
    }
}
