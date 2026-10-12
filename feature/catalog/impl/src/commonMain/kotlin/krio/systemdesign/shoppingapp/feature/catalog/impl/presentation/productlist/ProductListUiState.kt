package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import androidx.compose.foundation.text.input.TextFieldState

internal data class ProductListUiState(
    // A state holder, not a String: the field edits it in place, so typing never waits for this flow
    // and can't lose characters. It's the same instance for the whole screen.
    val searchQuery: TextFieldState,
    // How many of each product are in the cart, by product id; a product that isn't in the cart isn't here.
    val cartQuantities: Map<String, Int> = emptyMap(),
)
