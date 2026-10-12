package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

import krio.systemdesign.shoppingapp.core.composeutils.text.AndroidUiText

internal sealed interface ProductDetailsEffect {

    data object NavigateBack : ProductDetailsEffect

    data class ShowSnackBar(val message: AndroidUiText) : ProductDetailsEffect
}
