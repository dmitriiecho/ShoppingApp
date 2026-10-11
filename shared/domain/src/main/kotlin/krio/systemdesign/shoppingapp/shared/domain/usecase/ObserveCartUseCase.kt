package krio.systemdesign.shoppingapp.shared.domain.usecase

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
class ObserveCartUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Cart> = cartRepository.observe()
}
