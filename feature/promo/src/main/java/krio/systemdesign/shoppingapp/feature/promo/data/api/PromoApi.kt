package krio.systemdesign.shoppingapp.feature.promo.data.api

import krio.systemdesign.shoppingapp.feature.promo.data.dto.PromoCodeDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PromoApi {
    @GET("promo-codes/{code}")
    suspend fun checkPromoCode(
        @Path("code") code: String,
    ): Response<PromoCodeDTO>
}
