package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

class GetProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: String): ProductLoadResult = productRepository.getProduct(productId)
}
