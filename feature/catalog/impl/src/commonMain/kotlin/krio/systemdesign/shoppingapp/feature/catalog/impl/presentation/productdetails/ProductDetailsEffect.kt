package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface ProductDetailsEffect {

    data object NavigateBack : ProductDetailsEffect

    data class ShowSnackBar(val message: UiText) : ProductDetailsEffect
}
