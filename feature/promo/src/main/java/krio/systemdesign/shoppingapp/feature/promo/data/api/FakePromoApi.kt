package krio.systemdesign.shoppingapp.feature.promo.data.api

import kotlinx.coroutines.delay
import krio.systemdesign.shoppingapp.feature.promo.data.dto.PromoCodeDTO
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NOT_FOUND

internal class FakePromoApi : PromoApi {

    override suspend fun checkPromoCode(code: String): Response<PromoCodeDTO> {
        delay(NETWORK_DELAY_MS)
        // Как настоящий сервер: на неизвестный код отвечаем 404.
        val promoCode = PROMO_CODES.find { it.code == code }
            ?: return Response.error(HTTP_NOT_FOUND, "".toResponseBody())
        return Response.success(promoCode)
    }

    private companion object {
        const val NETWORK_DELAY_MS = 1500L

        val PROMO_CODES = listOf(
            PromoCodeDTO(code = "SALE10", discountPercent = 10),
            PromoCodeDTO(code = "SALE25", discountPercent = 25),
        )
    }
}
