package krio.systemdesign.shoppingapp.feature.cart.impl.domain.model

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import java.io.IOException
import kotlin.test.Test
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem

class CartValidationTest {

    @Test
    fun `price change stays pending while the item has the old price`() {
        val issue = ItemIssue.PriceChanged("1", newPrice = 1200)
        val cart = testCart(testCartItem(productId = "1", price = 1000))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).containsExactly(issue)
    }

    @Test
    fun `price change is fixed once the item has the new price`() {
        val issue = ItemIssue.PriceChanged("1", newPrice = 1200)
        val cart = testCart(testCartItem(productId = "1", price = 1200))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).isEmpty()
    }

    @Test
    fun `missing stock stays pending while the cart holds more than the stock`() {
        val issue = ItemIssue.NotEnoughStock("1", availableQuantity = 2)
        val cart = testCart(testCartItem(productId = "1", quantity = 3))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).containsExactly(issue)
    }

    @Test
    fun `missing stock is fixed once the quantity fits the stock`() {
        val issue = ItemIssue.NotEnoughStock("1", availableQuantity = 2)
        val cart = testCart(testCartItem(productId = "1", quantity = 2))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).isEmpty()
    }

    @Test
    fun `unavailable item stays pending while it is in the cart`() {
        val issue = ItemIssue.Unavailable("1")
        val cart = testCart(testCartItem(productId = "1"))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).containsExactly(issue)
    }

    @Test
    fun `issue of an item removed from the cart is fixed`() {
        val issue = ItemIssue.Unavailable("1")
        val cart = testCart(testCartItem(productId = "2"))

        assertThat(CartValidation(issues = listOf(issue)).pendingIssues(cart)).isEmpty()
    }

    @Test
    fun `promo code is valid when the validation found nothing wrong with it`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidation().isPromoCodeValid(cart)).isTrue()
    }

    @Test
    fun `promo code found invalid stays invalid while it is applied`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidation(invalidPromoCode = "SALE10").isPromoCodeValid(cart)).isFalse()
    }

    @Test
    fun `promo code applied after the validation is valid`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE25", 25))

        assertThat(CartValidation(invalidPromoCode = "SALE10").isPromoCodeValid(cart)).isTrue()
    }

    @Test
    fun `invalid promo code gives no discount`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidation(invalidPromoCode = "SALE10").discount(cart)).isEqualTo(0)
    }

    @Test
    fun `total with an invalid promo code is the subtotal`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidation(invalidPromoCode = "SALE10").totalPrice(cart)).isEqualTo(2000)
    }

    @Test
    fun `stock of an item without issues is the one saved in the cart`() {
        val item = testCartItem(productId = "1", availableQuantity = 7)

        assertThat(CartValidation().stockOf(item)).isEqualTo(7)
    }

    @Test
    fun `stock of an unavailable item is 0`() {
        val item = testCartItem(productId = "1", availableQuantity = 7)

        assertThat(CartValidation(issues = listOf(ItemIssue.Unavailable("1"))).stockOf(item)).isEqualTo(0)
    }

    // Once the user lowers the quantity to the stock, "+" must not let them go above it again.
    @Test
    fun `stock reported by the validation holds after the change is fixed`() {
        val validation = CartValidation(issues = listOf(ItemIssue.NotEnoughStock("1", availableQuantity = 2)))
        val item = testCartItem(productId = "1", quantity = 2, availableQuantity = 7)

        assertThat(validation.stockOf(item)).isEqualTo(2)
    }

    @Test
    fun `cart with everything fixed and a working promo code allows checkout`() {
        val cart = testCart(testCartItem(productId = "1", price = 1200), promoCode = PromoCode("SALE10", 10))
        val validation = CartValidation(issues = listOf(ItemIssue.PriceChanged("1", newPrice = 1200)))

        assertThat(validation.allowsCheckout(cart)).isTrue()
    }

    @Test
    fun `empty cart does not allow checkout`() {
        assertThat(CartValidation().allowsCheckout(testCart())).isFalse()
    }

    @Test
    fun `pending change blocks checkout`() {
        val cart = testCart(testCartItem(productId = "1"))

        assertThat(CartValidation(issues = listOf(ItemIssue.Unavailable("1"))).allowsCheckout(cart)).isFalse()
    }

    @Test
    fun `invalid promo code blocks checkout`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidation(invalidPromoCode = "SALE10").allowsCheckout(cart)).isFalse()
    }

    @Test
    fun `successful validation leaves nothing to fix`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE10", 10))

        assertThat(CartValidationResult.Success.toCartValidation(cart)).isEqualTo(CartValidation())
    }

    @Test
    fun `invalid result keeps its issues and the code it found invalid`() {
        val cart = testCart(testCartItem(productId = "1"), promoCode = PromoCode("SALE10", 10))
        val result = CartValidationResult.Invalid(listOf(ItemIssue.Unavailable("1")), isPromoCodeValid = false)

        assertThat(result.toCartValidation(cart))
            .isEqualTo(CartValidation(issues = listOf(ItemIssue.Unavailable("1")), invalidPromoCode = "SALE10"))
    }

    @Test
    fun `invalid result with a working promo code marks no code invalid`() {
        val cart = testCart(testCartItem(productId = "1"), promoCode = PromoCode("SALE10", 10))
        val result = CartValidationResult.Invalid(listOf(ItemIssue.Unavailable("1")), isPromoCodeValid = true)

        assertThat(result.toCartValidation(cart)?.invalidPromoCode).isNull()
    }

    // null tells the caller to keep the last validation: a lost connection says nothing new about the cart.
    @Test
    fun `network error gives no new validation`() {
        val result = CartValidationResult.Error(IOException("No network"))

        assertThat(result.toCartValidation(testCart(testCartItem()))).isNull()
    }
}
