package krio.systemdesign.shoppingapp.feature.cart.domain.usecase

import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(): Result<Unit> = cartRepository.clearItems()
}
