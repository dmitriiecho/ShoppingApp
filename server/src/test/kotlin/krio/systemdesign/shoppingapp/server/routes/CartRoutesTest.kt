package krio.systemdesign.shoppingapp.server.routes

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.ItemIssueDTO
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.validateCart

class CartRoutesTest {

    @Test
    fun `cart matching the catalog`() = serverTest { client ->
        val response = client.validateCart(
            promoCode = null,
            items = listOf(CartItemDTO("1", price = 1000, quantity = 5)),
        )
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(CartValidationResponseDTO(issues = emptyList(), promoCodeValid = true), response.body())
    }

    @Test
    fun `out-of-stock and unknown product - unavailable without price and stock`() = serverTest { client ->
        assertEquals(
            listOf(ItemIssueDTO.Unavailable("3"), ItemIssueDTO.Unavailable("42")),
            client.cartIssues(CartItemDTO("3", price = 1, quantity = 2), CartItemDTO("42", price = 1000, quantity = 1)),
        )
    }

    @Test
    fun `more in the cart than in stock`() = serverTest { client ->
        assertEquals(
            listOf(ItemIssueDTO.NotEnoughStock("2", availableQuantity = 2)),
            client.cartIssues(CartItemDTO("2", price = 1000, quantity = 3)),
        )
    }

    @Test
    fun `price changed`() = serverTest { client ->
        assertEquals(
            listOf(ItemIssueDTO.PriceChanged("1", newPrice = 1000)),
            client.cartIssues(CartItemDTO("1", price = 900, quantity = 1)),
        )
    }

    @Test
    fun `both price and stock changed`() = serverTest { client ->
        assertEquals(
            listOf(
                ItemIssueDTO.PriceChanged("2", newPrice = 1000),
                ItemIssueDTO.NotEnoughStock("2", availableQuantity = 2),
            ),
            client.cartIssues(CartItemDTO("2", price = 900, quantity = 3)),
        )
    }

    @Test
    fun `cart promo code ignores case`() = serverTest { client ->
        suspend fun promoCodeValid(code: String?) =
            client.validateCart(promoCode = code).body<CartValidationResponseDTO>().promoCodeValid

        assertTrue(promoCodeValid("SALE10"))
        assertTrue(promoCodeValid("sale10"))
        assertFalse(promoCodeValid("SALE99"))
        // No code, nothing to check.
        assertTrue(promoCodeValid(null))
    }

    @Test
    fun `malformed cart - 400`() = serverTest { client ->
        val response = client.post("/cart/validate") {
            contentType(ContentType.Application.Json)
            setBody("""{"items": "oops"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `issues JSON format`() {
        val response = CartValidationResponseDTO(
            issues = listOf(
                ItemIssueDTO.Unavailable("3"),
                ItemIssueDTO.PriceChanged("1", newPrice = 900),
                ItemIssueDTO.NotEnoughStock("2", availableQuantity = 2),
            ),
            promoCodeValid = false,
        )
        assertEquals(
            """{"issues":[{"type":"unavailable","productId":"3"},""" +
                """{"type":"priceChanged","productId":"1","newPrice":900},""" +
                """{"type":"notEnoughStock","productId":"2","availableQuantity":2}],"promoCodeValid":false}""",
            Json.encodeToString(response),
        )
    }

    private suspend fun HttpClient.cartIssues(vararg items: CartItemDTO): List<ItemIssueDTO> =
        validateCart(promoCode = null, items = items.toList()).body<CartValidationResponseDTO>().issues
}
