package krio.systemdesign.shoppingapp.feature.catalog.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(productId: String): Result<Product> =
        productRepository.getProduct(productId)
}
