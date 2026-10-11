package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

@Inject
internal class GetProductUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: String): ProductLoadResult = productRepository.getProduct(productId)
}
