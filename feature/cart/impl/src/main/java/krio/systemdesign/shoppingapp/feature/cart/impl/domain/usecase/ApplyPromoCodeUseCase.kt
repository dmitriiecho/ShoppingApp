package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Inject
internal class ApplyPromoCodeUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(promoCode: PromoCode): Result<Unit> = cartRepository.applyPromoCode(promoCode)
}
