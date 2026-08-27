package krio.systemdesign.shoppingapp.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<Cart> = cartRepository.observe()
}
