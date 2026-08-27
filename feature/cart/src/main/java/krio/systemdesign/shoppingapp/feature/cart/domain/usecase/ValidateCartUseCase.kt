package krio.systemdesign.shoppingapp.feature.cart.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class ValidateCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(): CartValidationResult = cartRepository.validate()
}
