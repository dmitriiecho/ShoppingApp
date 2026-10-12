package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository

import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage

// Answers as the test decides. A test can suspend in an answer to act while the request is in progress.
internal class TestProductRepository : ProductRepository {
    var productsAnswer: suspend (query: String, page: Int) -> Result<ProductsPage> =
        { _, _ -> Result.success(ProductsPage(emptyList(), endReached = true)) }
    var productAnswer: suspend (productId: String) -> ProductLoadResult = { ProductLoadResult.NotFound }

    // Every page asked for, as query to page number, in order.
    val pageRequests = mutableListOf<Pair<String, Int>>()

    override suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage> {
        pageRequests += query to page
        return productsAnswer(query, page)
    }

    override suspend fun getProduct(productId: String): ProductLoadResult = productAnswer(productId)
}
