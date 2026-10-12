package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

@Inject
internal class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<ProductsPage> = productRepository.getProducts(query, page, pageSize)
}
