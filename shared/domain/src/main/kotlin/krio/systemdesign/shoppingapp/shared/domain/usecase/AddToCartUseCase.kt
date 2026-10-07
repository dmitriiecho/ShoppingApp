package krio.systemdesign.shoppingapp.shared.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class AddToCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(
        product: Product,
        quantity: Int = 1,
    ): Result<Unit> = cartRepository.addItem(product, quantity)
}
