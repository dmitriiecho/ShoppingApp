package krio.systemdesign.shoppingapp.shared.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class ObserveCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Cart> = cartRepository.observe()
}
