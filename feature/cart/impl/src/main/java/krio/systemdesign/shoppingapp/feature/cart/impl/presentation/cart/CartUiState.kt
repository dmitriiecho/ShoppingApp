package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import kotlinx.collections.immutable.ImmutableList

internal data class CartUiState(
    val content: Content = Content.Loading,
    // The user tapped Checkout; checkout opens only once the server confirms the cart, so meanwhile
    // the button shows a loader and ignores more taps.
    val isOpeningCheckout: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    // False both while the cart is loading and when it's empty: the bars have nothing to act on either way.
    val hasItems: Boolean
        get() = content is Content.Loaded && content.items.isNotEmpty()

    val canCheckout: Boolean
        get() = content is Content.Loaded && content.allowsCheckout && !isOpeningCheckout

    // The cart: Loading until Room returns it, so an empty list always means an empty cart.
    sealed interface Content {
        data object Loading : Content

        data class Loaded(
            val items: List<Item>,
            // Outlives the items: the empty cart shows it too.
            val promoCode: AppliedPromoCode?,
            val totals: Totals,
            val changes: Changes,
            // Every change the validation found is fixed and the promo code works.
            val allowsCheckout: Boolean,
        ) : Content
    }

    // Plain values and ImmutableList, not :shared:domain models: Compose can compare these by content,
    // so "+" on one card doesn't redraw the others.
    data class Item(
        val productId: String,
        val name: String,
        val imageUrl: String,
        val price: Long,
        val quantity: Int,
        // Changes the last validation found and the user hasn't fixed yet; one item can have several.
        val issues: ImmutableList<Issue>,
        val canAddOneMore: Boolean,
    )

    sealed interface Issue {
        data object Unavailable : Issue

        data class PriceChanged(val newPrice: Long) : Issue

        data class NotEnoughStock(val availableQuantity: Int) : Issue
    }

    // Plain values for the same reason as Item.
    data class AppliedPromoCode(
        val code: String,
        val discountPercent: Int,
        // False when the validation found the code no longer works; it stays until the user removes it.
        val isValid: Boolean,
    )

    // discount is null when nothing is subtracted: no promo code or an invalid one.
    data class Totals(
        val subtotal: Long,
        val discount: Long?,
        val total: Long,
    )

    // How many items each kind of unfixed change touches; shown above the total.
    data class Changes(
        val priceChangeCount: Int,
        val unavailableItemCount: Int,
        val notEnoughStockItemCount: Int,
    ) {
        val hasAny: Boolean
            get() = priceChangeCount > 0 || unavailableItemCount > 0 || notEnoughStockItemCount > 0
    }
}
