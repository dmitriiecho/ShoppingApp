package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
internal class ValidateCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): CartValidationResult = cartRepository.validate()
}
