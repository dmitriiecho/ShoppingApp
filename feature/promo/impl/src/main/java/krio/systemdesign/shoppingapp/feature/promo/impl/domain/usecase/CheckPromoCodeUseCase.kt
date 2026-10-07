package krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository

internal class CheckPromoCodeUseCase @Inject constructor(private val promoCodeRepository: PromoCodeRepository) {
    suspend operator fun invoke(code: String): PromoCodeCheckResult = promoCodeRepository.checkPromoCode(code)
}
