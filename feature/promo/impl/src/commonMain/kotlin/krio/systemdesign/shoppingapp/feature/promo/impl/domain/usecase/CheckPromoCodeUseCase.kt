package krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository

@Inject
internal class CheckPromoCodeUseCase(private val promoCodeRepository: PromoCodeRepository) {
    suspend operator fun invoke(code: String): PromoCodeCheckResult = promoCodeRepository.checkPromoCode(code)
}
