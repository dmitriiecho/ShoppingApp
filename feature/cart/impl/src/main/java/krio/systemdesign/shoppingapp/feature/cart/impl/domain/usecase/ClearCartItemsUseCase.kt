package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
internal class ClearCartItemsUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): Result<Unit> = cartRepository.clearItems()
}
