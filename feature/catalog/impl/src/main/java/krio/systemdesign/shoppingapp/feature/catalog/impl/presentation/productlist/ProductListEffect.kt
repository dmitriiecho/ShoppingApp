package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface ProductListEffect {

    data class NavigateToDetails(
        val productId: String,
        val productName: String,
        val imageUrl: String,
    ) : ProductListEffect

    data object NavigateBack : ProductListEffect

    data class ShowSnackBar(val message: UiText) : ProductListEffect
}
