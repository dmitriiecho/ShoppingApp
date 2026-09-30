package krio.systemdesign.shoppingapp.feature.catalog.domain.usecase

import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): Result<ProductsPage> =
        productRepository.getProducts(query, page, pageSize)
}
