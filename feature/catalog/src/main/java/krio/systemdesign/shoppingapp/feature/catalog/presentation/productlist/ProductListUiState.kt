package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

data class ProductListUiState(
    val searchQuery: String = "",
    val cartQuantities: ImmutableMap<String, Int> = persistentMapOf(),
)
