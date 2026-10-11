package krio.systemdesign.shoppingapp.feature.checkout.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
internal class PlaceOrderUseCase(private val cartRepository: CartRepository) {
    // Orders aren't sent anywhere: the server has no endpoint for them.
    // Placing one only resets the cart (items and promo code).
    suspend operator fun invoke(): Result<Unit> = cartRepository.reset()
}
