package krio.systemdesign.shoppingapp.feature.checkout.domain.usecase

import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(): Result<Unit> = cartRepository.clear()
}
