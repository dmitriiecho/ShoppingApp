package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class RemovePromoCodeUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): Result<Unit> = cartRepository.removePromoCode()
}
