package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.model.Product

sealed interface ProductDetailsUiState {
    val title: String

    data class Loading(
        override val title: String,
    ) : ProductDetailsUiState

    data class Content(
        val product: Product,
        val cartQuantity: Int,
    ) : ProductDetailsUiState {
        override val title: String = product.name
    }

    data class Error(
        override val title: String,
        val message: UiText,
    ) : ProductDetailsUiState
}
