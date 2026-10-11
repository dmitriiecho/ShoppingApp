package krio.systemdesign.shoppingapp.crossfeature.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.crossfeature.domain.repository.CartRepository

@Inject
class UpdateCartQuantityUseCase(private val cartRepository: CartRepository) {
    // Quantity 0 removes the item: "−" on the last one takes it out of the cart.
    suspend operator fun invoke(
        productId: String,
        quantity: Int,
    ): Result<Unit> = if (quantity <= 0) {
        cartRepository.removeItem(productId)
    } else {
        cartRepository.setQuantity(productId, quantity)
    }
}
