package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import krio.systemdesign.shoppingapp.core.ui.text.UiText

sealed interface CheckoutEffect {

    data object Close : CheckoutEffect

    data class ShowSnackBar(val message: UiText) : CheckoutEffect
}
