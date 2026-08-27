package krio.systemdesign.shoppingapp.domain.usecase

import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        productId: String,
        quantity: Int,
    ): Result<Unit> = cartRepository.setQuantity(productId, quantity)
}
