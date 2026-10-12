package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository

import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage

internal interface ProductRepository {
    // Pages start at 1. An empty query returns the whole catalog.
    suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage>

    suspend fun getProduct(productId: String): ProductLoadResult
}
