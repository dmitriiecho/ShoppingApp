package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

sealed interface ProductDetailsEffect {

    data object NavigateBack : ProductDetailsEffect

    data class ShowSnackBar(val message: String) : ProductDetailsEffect
}
