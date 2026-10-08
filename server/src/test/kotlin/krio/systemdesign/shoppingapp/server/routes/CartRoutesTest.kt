package krio.systemdesign.shoppingapp.server.routes

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import krio.systemdesign.shoppingapp.server.apiSample
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.ItemIssueDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import krio.systemdesign.shoppingapp.server.serverJson
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.testProduct
import krio.systemdesign.shoppingapp.server.testShopData
import krio.systemdesign.shoppingapp.server.validateCart

class CartRoutesTest {

    private val promoData = testShopData(
        products = listOf(testProduct(id = "1")),
        promoCodes = listOf(PromoCodeDTO("SALE10", 10)),
    )

    @Test
    fun `cart matching the catalog has no issues`() = serverTest(
        testShopData(products = listOf(testProduct(id = "1", price = 1000, availableQuantity = 10))),
    ) { client ->
        val response = client.validateCart(CartItemDTO("1", price = 1000, quantity = 5))

        assertThat(response.body<CartValidationResponseDTO>())
            .isEqualTo(CartValidationResponseDTO(issues = emptyList(), promoCodeValid = true))
    }

    @Test
    fun `out-of-stock product is reported unavailable, without price and stock`() = serverTest(
        testShopData(products = listOf(testProduct(id = "1", price = 1000, availableQuantity = 0))),
    ) { client ->
        val issues = client.cartIssues(CartItemDTO("1", price = 900, quantity = 2))

        assertThat(issues).containsExactly(ItemIssueDTO.Unavailable("1"))
    }

    @Test
    fun `unknown product is reported unavailable`() = serverTest { client ->
        val issues = client.cartIssues(CartItemDTO("42", price = 1000, quantity = 1))

        assertThat(issues).containsExactly(ItemIssueDTO.Unavailable("42"))
    }

    @Test
    fun `more in the cart than in stock is reported with the stock`() = serverTest(
        testShopData(products = listOf(testProduct(id = "1", availableQuantity = 2))),
    ) { client ->
        val issues = client.cartIssues(CartItemDTO("1", price = 1000, quantity = 3))

        assertThat(issues).containsExactly(ItemIssueDTO.NotEnoughStock("1", availableQuantity = 2))
    }

    @Test
    fun `changed price is reported with the new price`() = serverTest(
        testShopData(products = listOf(testProduct(id = "1", price = 1000))),
    ) { client ->
        val issues = client.cartIssues(CartItemDTO("1", price = 900, quantity = 1))

        assertThat(issues).containsExactly(ItemIssueDTO.PriceChanged("1", newPrice = 1000))
    }

    @Test
    fun `changed price and missing stock of one item are both reported`() = serverTest(
        testShopData(products = listOf(testProduct(id = "1", price = 1000, availableQuantity = 2))),
    ) { client ->
        val issues = client.cartIssues(CartItemDTO("1", price = 900, quantity = 3))

        assertThat(issues).containsExactly(
            ItemIssueDTO.PriceChanged("1", newPrice = 1000),
            ItemIssueDTO.NotEnoughStock("1", availableQuantity = 2),
        )
    }

    @Test
    fun `existing promo code is valid`() = serverTest(promoData) { client ->
        assertThat(client.promoCodeValid("SALE10")).isTrue()
    }

    @Test
    fun `promo code in another case is valid`() = serverTest(promoData) { client ->
        assertThat(client.promoCodeValid("sale10")).isTrue()
    }

    @Test
    fun `promo code with spaces around it is valid`() = serverTest(promoData) { client ->
        assertThat(client.promoCodeValid(" SALE10 ")).isTrue()
    }

    @Test
    fun `unknown promo code is invalid`() = serverTest(promoData) { client ->
        assertThat(client.promoCodeValid("SALE99")).isFalse()
    }

    // promoCodeValid answers "does the sent code still work"; with no code there is nothing that fails.
    @Test
    fun `cart without a promo code has a valid promo code`() = serverTest(promoData) { client ->
        assertThat(client.promoCodeValid(null)).isTrue()
    }

    @Test
    fun `malformed cart is rejected with 400`() = serverTest { client ->
        val response = client.post("/cart/validate") {
            contentType(ContentType.Application.Json)
            setBody("""{"items": "oops"}""")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    // The app sends a cart like the request sample; the server must answer like the response sample.
    @Test
    fun `cart check answers as in the API sample`() = serverTest(
        testShopData(
            products = listOf(
                testProduct(id = "1", price = 1299, availableQuantity = 12),
                testProduct(id = "2", price = 1000, availableQuantity = 2),
                testProduct(id = "3", price = 4999, availableQuantity = 0),
            ),
            promoCodes = listOf(PromoCodeDTO("SALE10", 10)),
        ),
    ) { client ->
        val response = client.post("/cart/validate") {
            contentType(ContentType.Application.Json)
            setBody(apiSample("cart-validation-request.json").toString())
        }

        assertThat(serverJson.parseToJsonElement(response.bodyAsText()))
            .isEqualTo(apiSample("cart-validation-response.json"))
    }

    private suspend fun HttpClient.cartIssues(vararg items: CartItemDTO): List<ItemIssueDTO> =
        validateCart(*items).body<CartValidationResponseDTO>().issues

    private suspend fun HttpClient.promoCodeValid(code: String?): Boolean =
        validateCart(CartItemDTO("1", price = 1000, quantity = 1), promoCode = code)
            .body<CartValidationResponseDTO>()
            .promoCodeValid
}
