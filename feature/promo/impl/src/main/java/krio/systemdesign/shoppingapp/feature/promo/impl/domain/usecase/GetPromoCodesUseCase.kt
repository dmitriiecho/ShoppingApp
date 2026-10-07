package krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

internal class GetPromoCodesUseCase @Inject constructor(private val promoCodeRepository: PromoCodeRepository) {
    suspend operator fun invoke(): Result<List<PromoCode>> = promoCodeRepository.getPromoCodes()
}
