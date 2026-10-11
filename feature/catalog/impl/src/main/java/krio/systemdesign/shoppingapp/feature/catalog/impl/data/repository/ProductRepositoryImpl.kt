package krio.systemdesign.shoppingapp.feature.catalog.impl.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.ktor.http.HttpStatusCode
import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.api.ProductApi
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class ProductRepositoryImpl(private val api: ProductApi) : ProductRepository {
    override suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage> {
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
                if (result.code == HttpStatusCode.NotFound.value) {
                    ProductLoadResult.NotFound
                } else {
                    ProductLoadResult.Error(result.error)
                }
            is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
        }
    }
}
