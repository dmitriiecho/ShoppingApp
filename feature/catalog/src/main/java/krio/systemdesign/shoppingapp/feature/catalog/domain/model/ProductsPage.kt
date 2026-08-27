package krio.systemdesign.shoppingapp.feature.catalog.domain.model

import krio.systemdesign.shoppingapp.domain.model.Product

data class ProductsPage(
    val products: List<Product>,
    val endReached: Boolean,
)
