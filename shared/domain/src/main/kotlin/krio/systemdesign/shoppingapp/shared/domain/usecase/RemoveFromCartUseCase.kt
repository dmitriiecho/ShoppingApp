package krio.systemdesign.shoppingapp.shared.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String): Result<Unit> = cartRepository.removeItem(productId)
}
