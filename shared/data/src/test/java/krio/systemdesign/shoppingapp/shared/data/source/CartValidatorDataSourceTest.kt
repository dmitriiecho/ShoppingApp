package krio.systemdesign.shoppingapp.shared.data.source

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.core.network.apiRequest
import krio.systemdesign.shoppingapp.core.network.apiSample
import krio.systemdesign.shoppingapp.core.network.networkJson
import krio.systemdesign.shoppingapp.core.network.networkTest
import krio.systemdesign.shoppingapp.shared.data.api.CartApi
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem

class CartValidatorDataSourceTest {

    private val cart = testCart(testCartItem(productId = "1"))

    @Test
    fun `cart without issues and with a working promo code is valid`() = networkTest(::CartApi) { server, api ->
        server.enqueue(body = """{"issues":[],"promoCodeValid":true}""")

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(CartValidationResult.Success)
    }

    @Test
    fun `cart with issues is invalid with those issues`() = networkTest(::CartApi) { server, api ->
        server.enqueue(
            body = """{"issues":[{"type":"priceChanged","productId":"1","newPrice":1200}],"promoCodeValid":true}""",
        )

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(
            CartValidationResult.Invalid(listOf(ItemIssue.PriceChanged("1", newPrice = 1200)), isPromoCodeValid = true),
        )
    }

    @Test
    fun `cart with a promo code that stopped working is invalid`() = networkTest(::CartApi) { server, api ->
        server.enqueue(body = """{"issues":[],"promoCodeValid":false}""")

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(CartValidationResult.Invalid(emptyList(), isPromoCodeValid = false))
    }

    @Test
    fun `server error leaves the cart unchecked`() = networkTest(::CartApi) { server, api ->
        server.enqueue(code = 500)

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isInstanceOf<CartValidationResult.Error>()
    }

    @Test
    fun `cart is sent to the address in the API sample`() = networkTest(::CartApi) { server, api ->
        server.enqueue(body = apiSample("cart-validation-response.json").toString())

        CartValidatorDataSource(api).validate(sampleCart)

        val request = server.takeRequest()
        assertThat("${request.method} ${request.target}").isEqualTo(apiRequest("cart-validation"))
    }

    @Test
    fun `cart is sent with the body in the API sample`() = networkTest(::CartApi) { server, api ->
        server.enqueue(body = apiSample("cart-validation-response.json").toString())

        CartValidatorDataSource(api).validate(sampleCart)

        val sent = networkJson.parseToJsonElement(server.takeRequest().body)
        assertThat(sent).isEqualTo(apiSample("cart-validation-request.json"))
    }

    @Test
    fun `answer from the API sample is read with all its changes`() = networkTest(::CartApi) { server, api ->
        server.enqueue(body = apiSample("cart-validation-response.json").toString())

        val result = CartValidatorDataSource(api).validate(sampleCart)

        assertThat(result).isEqualTo(
            CartValidationResult.Invalid(
                issues = listOf(
                    ItemIssue.PriceChanged("2", newPrice = 1000),
                    ItemIssue.NotEnoughStock("2", availableQuantity = 2),
                    ItemIssue.Unavailable("3"),
                ),
                isPromoCodeValid = false,
            ),
        )
    }

    // The cart behind the request sample.
    private val sampleCart = testCart(
        testCartItem(productId = "1", price = 1299, quantity = 1),
        testCartItem(productId = "2", price = 900, quantity = 3),
        testCartItem(productId = "3", price = 4999, quantity = 1),
        promoCode = PromoCode("SUMMER", 10),
    )
}
