package krio.systemdesign.shoppingapp.shared.data.source

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.core.network.networkTest
import krio.systemdesign.shoppingapp.shared.data.api.CartApi
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem
import mockwebserver3.MockResponse

class CartValidatorDataSourceTest {

    private val cart = testCart(testCartItem(productId = "1"))

    @Test
    fun `cart without issues and with a working promo code is valid`() = networkTest<CartApi> { server, api ->
        server.enqueue(MockResponse.Builder().body("""{"issues":[],"promoCodeValid":true}""").build())

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(CartValidationResult.Success)
    }

    @Test
    fun `cart with issues is invalid with those issues`() = networkTest<CartApi> { server, api ->
        server.enqueue(
            MockResponse.Builder()
                .body("""{"issues":[{"type":"priceChanged","productId":"1","newPrice":1200}],"promoCodeValid":true}""")
                .build(),
        )

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(
            CartValidationResult.Invalid(listOf(ItemIssue.PriceChanged("1", newPrice = 1200)), isPromoCodeValid = true),
        )
    }

    @Test
    fun `cart with a promo code that stopped working is invalid`() = networkTest<CartApi> { server, api ->
        server.enqueue(MockResponse.Builder().body("""{"issues":[],"promoCodeValid":false}""").build())

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isEqualTo(CartValidationResult.Invalid(emptyList(), isPromoCodeValid = false))
    }

    @Test
    fun `server error leaves the cart unchecked`() = networkTest<CartApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(500).build())

        val result = CartValidatorDataSource(api).validate(cart)

        assertThat(result).isInstanceOf<CartValidationResult.Error>()
    }
}
