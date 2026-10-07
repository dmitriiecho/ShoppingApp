package krio.systemdesign.shoppingapp.server

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

val TEST_DATA = ShopData(
    products = listOf(
        testProduct(id = "1", name = "Red Mug"),
        testProduct(id = "2", name = "Blue Mug", availableQuantity = 2),
        testProduct(id = "3", name = "Lamp", availableQuantity = 0),
    ),
    promoCodes = listOf(PromoCodeDTO("SALE10", 10), PromoCodeDTO("SALE25", 25)),
)

fun testProduct(
    id: String,
    name: String,
    availableQuantity: Int = 10,
) = ProductDTO(
    id = id,
    name = name,
    price = 1000,
    imageUrl = "https://picsum.photos/seed/$id/400/400",
    description = "",
    availableQuantity = availableQuantity,
)

// The server on TEST_DATA, with a client that reads JSON.
fun serverTest(block: suspend (HttpClient) -> Unit) = testApplication {
    application { module(TEST_DATA, createTempDirectory("product-images")) }
    block(createClient { install(ContentNegotiation) { json() } })
}

suspend fun HttpClient.validateCart(
    promoCode: String?,
    items: List<CartItemDTO> = listOf(CartItemDTO("1", price = 1000, quantity = 1)),
): HttpResponse = post("/cart/validate") {
    contentType(ContentType.Application.Json)
    setBody(CartValidationRequestDTO(items = items, promoCode = promoCode))
}
