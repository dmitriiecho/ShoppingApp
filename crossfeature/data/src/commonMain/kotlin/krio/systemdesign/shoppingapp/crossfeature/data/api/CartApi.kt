package krio.systemdesign.shoppingapp.crossfeature.data.api

import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import krio.systemdesign.shoppingapp.contract.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.contract.CartValidationResponseDTO

@Inject
internal class CartApi(private val client: HttpClient) {
    suspend fun validate(request: CartValidationRequestDTO): CartValidationResponseDTO = client.post("cart/validate") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()
}
