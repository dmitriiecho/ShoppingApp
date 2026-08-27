package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

sealed interface CheckoutEffect {

    data object Close : CheckoutEffect

    data class ShowSnackBar(val message: String) : CheckoutEffect
}
