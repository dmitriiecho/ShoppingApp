package krio.systemdesign.shoppingapp.feature.promo.impl.data.api

import krio.systemdesign.shoppingapp.feature.promo.impl.data.dto.PromoCodeDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PromoApi {
    @GET("promo-codes/{code}")
    suspend fun checkPromoCode(@Path("code") code: String): Response<PromoCodeDTO>

    // Every code on the server, for the hint on the promo code screen.
    @GET("promo-codes")
    suspend fun getPromoCodes(): Response<List<PromoCodeDTO>>
}
