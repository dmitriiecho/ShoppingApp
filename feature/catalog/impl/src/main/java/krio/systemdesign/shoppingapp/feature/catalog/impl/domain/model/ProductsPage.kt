package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model

import krio.systemdesign.shoppingapp.shared.domain.model.Product

data class ProductsPage(
    val products: List<Product>,
    val endReached: Boolean,
)
