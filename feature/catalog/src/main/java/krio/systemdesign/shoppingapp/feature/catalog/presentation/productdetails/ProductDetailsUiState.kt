package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.model.Product

sealed interface ProductDetailsUiState {
    val productId: String
    val title: String
    // Пустая, если картинка неизвестна: товар ещё не загружен, а маршрут её не передал (ссылка, корзина).
    val imageUrl: String

    data class Loading(
        override val productId: String,
        override val title: String,
        override val imageUrl: String,
    ) : ProductDetailsUiState

    data class Content(
        val product: Product,
        val cartQuantity: Int,
    ) : ProductDetailsUiState {
        override val productId: String = product.id
        override val title: String = product.name
        override val imageUrl: String = product.imageUrl
    }

    data class Error(
        override val productId: String,
        override val title: String,
        override val imageUrl: String,
        val message: UiText,
    ) : ProductDetailsUiState
}
