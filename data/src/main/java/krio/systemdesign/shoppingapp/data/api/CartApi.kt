package krio.systemdesign.shoppingapp.data.api

import krio.systemdesign.shoppingapp.data.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.data.dto.CartValidationResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface CartApi {
    @POST("cart/validate")
    suspend fun validate(
        @Body request: CartValidationRequestDTO,
    ): Response<CartValidationResponseDTO>
}
