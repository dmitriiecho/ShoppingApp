package krio.systemdesign.shoppingapp.feature.catalog.data.repository

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.data.api.ProductsApi
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val api: ProductsApi,
) : ProductRepository {
    override suspend fun getProducts(query: String, page: Int, pageSize: Int): Result<ProductsPage> {
        return try {
            val dto = api.getProducts(query, page, pageSize)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProduct(productId: String): Result<Product> {
        return try {
            Result.success(api.getProduct(productId).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
