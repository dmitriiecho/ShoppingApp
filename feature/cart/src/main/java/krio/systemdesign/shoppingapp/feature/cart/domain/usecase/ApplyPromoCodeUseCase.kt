package krio.systemdesign.shoppingapp.feature.cart.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class ApplyPromoCodeUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(promoCode: PromoCode): Result<Unit> =
        cartRepository.applyPromoCode(promoCode)
}
