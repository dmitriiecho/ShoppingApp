package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

internal class ValidateCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): CartValidationResult = cartRepository.validate()
}
