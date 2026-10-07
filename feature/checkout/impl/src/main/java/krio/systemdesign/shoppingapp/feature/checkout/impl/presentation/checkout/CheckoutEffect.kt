package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface CheckoutEffect {

    data object Close : CheckoutEffect

    data class ShowSnackBar(val message: UiText) : CheckoutEffect
}
