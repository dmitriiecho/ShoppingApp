package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface CartEffect {

    data object NavigateBack : CartEffect

    data object NavigateToCheckout : CartEffect

    data object NavigateToPromo : CartEffect

    data class NavigateToProduct(
        val productId: String,
        val productName: String,
        val imageUrl: String,
    ) : CartEffect

    data class ShowSnackBar(val message: UiText) : CartEffect
}
