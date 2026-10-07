package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

internal class GetProductsUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage> = productRepository.getProducts(query, page, pageSize)
}
