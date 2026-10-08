package krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository

import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

// Answers as the test decides. A test can suspend in an answer to act while the request is in progress.
internal class TestPromoCodeRepository : PromoCodeRepository {
    var checkAnswer: suspend (code: String) -> PromoCodeCheckResult = { PromoCodeCheckResult.NotFound }
    var promoCodesAnswer: suspend () -> Result<List<PromoCode>> = { Result.success(emptyList()) }

    // Every code sent for a check, in order.
    val checkedCodes = mutableListOf<String>()

    override suspend fun checkPromoCode(code: String): PromoCodeCheckResult {
        checkedCodes += code
        return checkAnswer(code)
    }

    override suspend fun getPromoCodes(): Result<List<PromoCode>> = promoCodesAnswer()
}
