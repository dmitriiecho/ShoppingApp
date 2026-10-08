package krio.systemdesign.shoppingapp.shared.domain.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CartTest {

    @Test
    fun `subtotal sums price times quantity of every item`() {
        val cart = testCart(
            testCartItem(productId = "1", price = 1000, quantity = 2),
            testCartItem(productId = "2", price = 250, quantity = 3),
        )

        assertThat(cart.subtotal()).isEqualTo(2750)
    }

    @Test
    fun `discount is the promo code percent of the subtotal`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        assertThat(cart.discount()).isEqualTo(200)
    }

    @Test
    fun `cart without a promo code has no discount`() {
        val cart = testCart(testCartItem(price = 2000))

        assertThat(cart.discount()).isEqualTo(0)
    }

    @Test
    fun `total is the subtotal minus the discount`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        assertThat(cart.totalPrice()).isEqualTo(1800)
    }

    @Test
    fun `quantityOf returns the quantity of a product in the cart`() {
        val cart = testCart(testCartItem(productId = "1", quantity = 3))

        assertThat(cart.quantityOf("1")).isEqualTo(3)
    }

    @Test
    fun `quantityOf returns 0 for a product not in the cart`() {
        val cart = testCart(testCartItem(productId = "1"))

        assertThat(cart.quantityOf("2")).isEqualTo(0)
    }
}
