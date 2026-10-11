package krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Inject
internal class GetPromoCodesUseCase(private val promoCodeRepository: PromoCodeRepository) {
    suspend operator fun invoke(): Result<List<PromoCode>> = promoCodeRepository.getPromoCodes()
}
