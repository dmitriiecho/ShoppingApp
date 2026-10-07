package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import kotlinx.collections.immutable.toImmutableList
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.model.CartValidation
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.canAddOneMore

// Lays the cart and its last validation out for the screen. The rules (what's still unfixed, which stock
// limits "+", whether the promo code gives a discount) are CartValidation's; this only maps its answers.

internal fun Cart.toContent(validation: CartValidation): CartUiState.Content.Loaded {
    val pendingIssues = validation.pendingIssues(this)
    val issuesByItem = pendingIssues.groupBy { it.productId }
    val isPromoCodeValid = validation.isPromoCodeValid(this)
    return CartUiState.Content.Loaded(
        items = items.map { item ->
            CartUiState.Item(
                productId = item.productId,
                name = item.name,
                imageUrl = item.imageUrl,
                price = item.price,
                quantity = item.quantity,
                issues = issuesByItem[item.productId].orEmpty().map { it.toUi() }.toImmutableList(),
                canAddOneMore = canAddOneMore(inCart = item.quantity, stock = validation.stockOf(item)),
            )
        },
        promoCode = promoCode?.let {
            CartUiState.AppliedPromoCode(
                code = it.code,
                discountPercent = it.discountPercent,
                isValid = isPromoCodeValid,
            )
        },
        totals = CartUiState.Totals(
            subtotal = subtotal(),
            // No discount line without a code that works.
            discount = validation.discount(this).takeIf { promoCode != null && isPromoCodeValid },
            total = validation.totalPrice(this),
        ),
        changes = CartUiState.Changes(
            priceChangeCount = pendingIssues.count { it is ItemIssue.PriceChanged },
            unavailableItemCount = pendingIssues.count { it is ItemIssue.Unavailable },
            notEnoughStockItemCount = pendingIssues.count { it is ItemIssue.NotEnoughStock },
        ),
        allowsCheckout = validation.allowsCheckout(this),
    )
}

private fun ItemIssue.toUi(): CartUiState.Issue = when (this) {
    is ItemIssue.Unavailable -> CartUiState.Issue.Unavailable
    is ItemIssue.PriceChanged -> CartUiState.Issue.PriceChanged(newPrice)
    is ItemIssue.NotEnoughStock -> CartUiState.Issue.NotEnoughStock(availableQuantity)
}
