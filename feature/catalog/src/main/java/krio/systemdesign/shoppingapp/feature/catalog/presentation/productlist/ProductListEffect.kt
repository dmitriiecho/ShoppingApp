package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

sealed interface ProductListEffect {

    data class NavigateToDetails(
        val productId: String,
        val productName: String,
    ) : ProductListEffect

    data object NavigateBack : ProductListEffect

    data class ShowSnackBar(val message: String) : ProductListEffect
}
