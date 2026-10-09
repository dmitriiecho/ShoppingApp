package krio.systemdesign.shoppingapp.server

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

// A sample from api-samples/: the JSON the app and the server agree on. The app's tests read the same files.
fun apiSample(name: String): JsonElement = serverJson.parseToJsonElement(Path("api-samples", name).readText())

// Sends a request from api-samples/requests.json ("GET /products/1"), the one the app sends, with a sample body.
// The app expects a success, so any other status fails the test with the status and the request in its message.
suspend fun HttpClient.sendApiRequest(
    name: String,
    body: JsonElement? = null,
): HttpResponse {
    val (method, target) = apiSample("requests.json").jsonObject.getValue(name).jsonPrimitive.content.split(" ")
    return request(target) {
        this.method = HttpMethod.parse(method)
        expectSuccess = true
        if (body != null) {
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }
    }
}
