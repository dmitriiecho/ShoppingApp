package krio.systemdesign.shoppingapp.crossfeature.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.crossfeature.domain.repository.CartRepository

@Inject
class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String): Result<Unit> = cartRepository.removeItem(productId)
}
