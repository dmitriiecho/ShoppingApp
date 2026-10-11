package krio.systemdesign.shoppingapp.crossfeature.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.crossfeature.domain.model.Product
import krio.systemdesign.shoppingapp.crossfeature.domain.repository.CartRepository

@Inject
class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int = 1,
    ): Result<Unit> = cartRepository.addItem(product, quantity)
}
