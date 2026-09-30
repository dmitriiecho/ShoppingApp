package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

import krio.systemdesign.shoppingapp.core.ui.text.UiText

sealed interface ProductDetailsEffect {

    data object NavigateBack : ProductDetailsEffect

    data class ShowSnackBar(val message: UiText) : ProductDetailsEffect
}
