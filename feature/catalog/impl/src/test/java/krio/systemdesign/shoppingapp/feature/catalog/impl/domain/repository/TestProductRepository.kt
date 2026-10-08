package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository

import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage

// Serves the pages the test puts in pages; with error set, every page load fails with it.
internal class TestProductRepository : ProductRepository {
    val pages = mutableMapOf<Int, ProductsPage>()
    var error: Throwable? = null

    override suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage> = error?.let { Result.failure(it) } ?: Result.success(pages.getValue(page))

    override suspend fun getProduct(productId: String): ProductLoadResult =
        throw UnsupportedOperationException("No test loads a single product yet")
}
