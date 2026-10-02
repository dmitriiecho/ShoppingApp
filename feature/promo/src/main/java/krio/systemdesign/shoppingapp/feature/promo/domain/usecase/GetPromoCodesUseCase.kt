package krio.systemdesign.shoppingapp.feature.promo.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.promo.domain.repository.PromoCodeRepository
import javax.inject.Inject

class GetPromoCodesUseCase @Inject constructor(
    private val promoCodeRepository: PromoCodeRepository,
) {
    suspend operator fun invoke(): Result<List<PromoCode>> =
        promoCodeRepository.getPromoCodes()
}
