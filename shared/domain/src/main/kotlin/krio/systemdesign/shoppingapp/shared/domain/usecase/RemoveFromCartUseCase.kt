package krio.systemdesign.shoppingapp.shared.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class RemoveFromCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String): Result<Unit> = cartRepository.removeItem(productId)
}
