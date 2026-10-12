package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import krio.systemdesign.shoppingapp.core.composeutils.text.AndroidUiText

internal sealed interface CheckoutEffect {

    data object Close : CheckoutEffect

    data class ShowSnackBar(val message: AndroidUiText) : CheckoutEffect
}
