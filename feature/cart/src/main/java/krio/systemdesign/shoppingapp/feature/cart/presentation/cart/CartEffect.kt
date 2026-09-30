package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import krio.systemdesign.shoppingapp.core.ui.text.UiText

sealed interface CartEffect {

    data object NavigateBack : CartEffect

    data object NavigateToCheckout : CartEffect

    data object NavigateToPromo : CartEffect

    data class NavigateToProduct(
        val productId: String,
        val productName: String,
    ) : CartEffect

    data class ShowSnackBar(val message: UiText) : CartEffect
}
