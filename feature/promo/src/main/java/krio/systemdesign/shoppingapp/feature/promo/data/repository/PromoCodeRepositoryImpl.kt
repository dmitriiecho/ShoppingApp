package krio.systemdesign.shoppingapp.feature.promo.data.repository

import android.util.Log
import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.feature.promo.data.api.PromoApi
import krio.systemdesign.shoppingapp.feature.promo.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.promo.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.domain.repository.PromoCodeRepository
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject

class PromoCodeRepositoryImpl @Inject constructor(
    private val api: PromoApi,
) : PromoCodeRepository {
    override suspend fun checkPromoCode(code: String): PromoCodeCheckResult {
        val result = networkCall { api.checkPromoCode(code) }
        return when (result) {
            // PromoCode не примет скидку вне 1..100: такой ответ сервера считаем сбоем.
            // runCatching здесь безопасен: внутри нет ожидания, и отмена сюда прийти не может.
            is NetworkResult.Success -> runCatching { result.body.toDomain() }.fold(
                onSuccess = { PromoCodeCheckResult.Valid(it) },
                onFailure = {
                    Log.w(TAG, "Сервер прислал некорректный промокод", it)
                    PromoCodeCheckResult.Error(it)
                },
            )
            is NetworkResult.HttpError ->
                if (result.code == HTTP_NOT_FOUND) PromoCodeCheckResult.NotFound
                else PromoCodeCheckResult.Error(result.error)
            is NetworkResult.Failure -> PromoCodeCheckResult.Error(result.error)
        }
    }
}

private const val TAG = "PromoCodeRepository"
