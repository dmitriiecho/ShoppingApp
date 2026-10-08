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
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

// The server in memory on the test's data, with a client that reads JSON as the app does.
fun serverTest(
    data: ShopData = testShopData(),
    imagesDir: Path = createTempDirectory("product-images"),
    block: suspend (client: HttpClient) -> Unit,
) = testApplication {
    application { module(data, imagesDir) }
    block(createClient { install(ContentNegotiation) { json(serverJson) } })
}

fun testShopData(
    products: List<ProductDTO> = emptyList(),
    promoCodes: List<PromoCodeDTO> = emptyList(),
) = ShopData(products = products, promoCodes = promoCodes)

fun testProduct(
    id: String = "1",
    name: String = "Product $id",
    price: Long = 1000,
    availableQuantity: Int = 10,
) = ProductDTO(
    id = id,
    name = name,
    price = price,
    imageUrl = "https://example.com/$id.png",
    description = "",
    availableQuantity = availableQuantity,
)

suspend fun HttpClient.validateCart(
    vararg items: CartItemDTO,
    promoCode: String? = null,
): HttpResponse = post("/cart/validate") {
    contentType(ContentType.Application.Json)
    setBody(CartValidationRequestDTO(items = items.toList(), promoCode = promoCode))
}
