package krio.systemdesign.shoppingapp.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int = 1,
    ): Result<Unit> = cartRepository.add(product, quantity)
}
