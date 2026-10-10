package krio.systemdesign.shoppingapp.shared.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.data.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.shared.data.dto.CartValidationResponseDTO

internal class CartApi @Inject constructor(private val client: HttpClient) {
    suspend fun validate(request: CartValidationRequestDTO): CartValidationResponseDTO = client.post("cart/validate") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()
}
