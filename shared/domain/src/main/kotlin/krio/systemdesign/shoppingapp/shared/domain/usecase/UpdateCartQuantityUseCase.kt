package krio.systemdesign.shoppingapp.shared.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class UpdateCartQuantityUseCase @Inject constructor(private val cartRepository: CartRepository) {
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
