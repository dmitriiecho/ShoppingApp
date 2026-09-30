package krio.systemdesign.shoppingapp.feature.catalog.data.repository

import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.feature.catalog.data.api.ProductsApi
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val api: ProductsApi,
) : ProductRepository {
    override suspend fun getProducts(query: String, page: Int, pageSize: Int): Result<ProductsPage> {
        val result = networkCall { api.getProducts(query, page, pageSize) }
        return when (result) {
            is NetworkResult.Success -> Result.success(result.body.toDomain())
            is NetworkResult.HttpError -> Result.failure(result.error)
            is NetworkResult.Failure -> Result.failure(result.error)
        }
    }

    override suspend fun getProduct(productId: String): ProductLoadResult {
        val result = networkCall { api.getProduct(productId) }
        return when (result) {
            is NetworkResult.Success -> ProductLoadResult.Success(result.body.toDomain())
            is NetworkResult.HttpError ->
                if (result.code == HTTP_NOT_FOUND) ProductLoadResult.NotFound
                else ProductLoadResult.Error(result.error)
            is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
        }
    }
}
