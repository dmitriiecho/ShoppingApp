package krio.systemdesign.shoppingapp.crossfeature.domain.usecase

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.crossfeature.domain.model.Cart
import krio.systemdesign.shoppingapp.crossfeature.domain.repository.CartRepository

@Inject
class ObserveCartUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Cart> = cartRepository.observe()
}
