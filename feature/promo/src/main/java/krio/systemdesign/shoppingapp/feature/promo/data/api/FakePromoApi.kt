package krio.systemdesign.shoppingapp.feature.promo.data.api

import kotlinx.coroutines.delay
import krio.systemdesign.shoppingapp.feature.promo.data.dto.PromoCodeDTO

internal class FakePromoApi : PromoApi {

    override suspend fun getPromoCode(code: String): PromoCodeDTO {
        delay(NETWORK_DELAY_MS)
        return PROMO_CODES.find { it.code == code }
            ?: error("Промокод не найден")
    }

    private companion object {
        const val NETWORK_DELAY_MS = 1500L

        val PROMO_CODES = listOf(
            PromoCodeDTO(code = "SALE10", discountPercent = 10),
            PromoCodeDTO(code = "SALE25", discountPercent = 25),
        )
    }
}
