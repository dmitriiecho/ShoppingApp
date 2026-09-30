package krio.systemdesign.shoppingapp.feature.catalog.domain.usecase

import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(productId: String): ProductLoadResult =
        productRepository.getProduct(productId)
}
