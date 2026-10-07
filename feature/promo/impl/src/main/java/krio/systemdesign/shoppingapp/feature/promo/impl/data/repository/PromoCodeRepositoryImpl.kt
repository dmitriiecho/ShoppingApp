package krio.systemdesign.shoppingapp.feature.promo.impl.data.repository

import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject
import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.feature.promo.impl.data.api.PromoApi
import krio.systemdesign.shoppingapp.feature.promo.impl.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import timber.log.Timber

internal class PromoCodeRepositoryImpl @Inject constructor(private val api: PromoApi) : PromoCodeRepository {
    override suspend fun checkPromoCode(code: String): PromoCodeCheckResult {
        val result = networkCall { api.checkPromoCode(code) }
        return when (result) {
            // PromoCode rejects a discount outside 1..100, so such a response counts as a failure.
            // runCatching is safe here: nothing suspends inside, so it can't swallow a cancellation.
            is NetworkResult.Success -> runCatching { result.body.toDomain() }.fold(
                onSuccess = { PromoCodeCheckResult.Valid(it) },
                onFailure = {
                    Timber.e(it, "Server sent an invalid promo code")
                    PromoCodeCheckResult.Error(it)
                },
            )
            is NetworkResult.HttpError ->
                if (result.code == HTTP_NOT_FOUND) {
                    PromoCodeCheckResult.NotFound
                } else {
                    PromoCodeCheckResult.Error(result.error)
                }
            is NetworkResult.Failure -> PromoCodeCheckResult.Error(result.error)
        }
    }

    override suspend fun getPromoCodes(): Result<List<PromoCode>> {
        val result = networkCall { api.getPromoCodes() }
        return when (result) {
            // As in checkPromoCode: a code with a discount outside 1..100 is a failure, the list isn't shown.
            is NetworkResult.Success -> runCatching { result.body.map { it.toDomain() } }
                .onFailure { Timber.e(it, "Server sent an invalid promo code") }
            is NetworkResult.HttpError -> Result.failure(result.error)
            is NetworkResult.Failure -> Result.failure(result.error)
        }
    }
}
