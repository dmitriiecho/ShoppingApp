package krio.systemdesign.shoppingapp.feature.catalog.domain.repository

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductsPage

interface ProductRepository {
    suspend fun getProducts(query: String, page: Int, pageSize: Int): Result<ProductsPage>

    suspend fun getProduct(productId: String): Result<Product>
}
