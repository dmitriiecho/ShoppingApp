package krio.systemdesign.shoppingapp.feature.promo.domain.usecase

import krio.systemdesign.shoppingapp.feature.promo.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.domain.repository.PromoCodeRepository
import javax.inject.Inject

class CheckPromoCodeUseCase @Inject constructor(
    private val promoCodeRepository: PromoCodeRepository,
) {
    suspend operator fun invoke(code: String): PromoCodeCheckResult =
        promoCodeRepository.checkPromoCode(code.trim().uppercase())
}
