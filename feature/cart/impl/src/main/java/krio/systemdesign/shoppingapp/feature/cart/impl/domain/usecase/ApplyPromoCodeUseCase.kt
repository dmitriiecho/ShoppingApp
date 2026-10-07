package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

internal class ApplyPromoCodeUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(promoCode: PromoCode): Result<Unit> = cartRepository.applyPromoCode(promoCode)
}
