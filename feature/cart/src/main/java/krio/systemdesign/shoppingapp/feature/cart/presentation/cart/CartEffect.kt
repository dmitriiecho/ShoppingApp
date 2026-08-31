package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

sealed interface CartEffect {

    data object NavigateBack : CartEffect

    data object NavigateToCheckout : CartEffect

    data object NavigateToPromo : CartEffect

    data class NavigateToProduct(
        val productId: String,
        val productName: String,
    ) : CartEffect

    data class ShowSnackBar(val message: String) : CartEffect
}
