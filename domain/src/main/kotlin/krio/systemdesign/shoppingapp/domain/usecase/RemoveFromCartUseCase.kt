package krio.systemdesign.shoppingapp.domain.usecase

import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        productId: String,
    ): Result<Unit> = cartRepository.remove(productId)
}
