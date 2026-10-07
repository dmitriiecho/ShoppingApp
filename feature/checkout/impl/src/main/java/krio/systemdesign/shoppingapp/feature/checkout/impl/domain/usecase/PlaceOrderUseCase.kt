package krio.systemdesign.shoppingapp.feature.checkout.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

internal class PlaceOrderUseCase @Inject constructor(private val cartRepository: CartRepository) {
    // Orders aren't sent anywhere: the server has no endpoint for them.
    // Placing one only resets the cart (items and promo code).
    suspend operator fun invoke(): Result<Unit> = cartRepository.reset()
}
