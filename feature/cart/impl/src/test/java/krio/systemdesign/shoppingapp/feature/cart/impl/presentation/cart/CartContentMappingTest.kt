package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.prop
import kotlin.test.Test
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.model.CartValidation
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem

class CartContentMappingTest {

    @Test
    fun `item shows its pending changes`() {
        val cart = testCart(testCartItem(productId = "1", price = 1000, quantity = 3))
        val validation = CartValidation(
            issues = listOf(
                ItemIssue.PriceChanged("1", newPrice = 1200),
                ItemIssue.NotEnoughStock("1", availableQuantity = 2),
            ),
        )

        val item = cart.toContent(validation).items.single()

        assertThat(item.issues).containsExactly(
            CartUiState.Issue.PriceChanged(newPrice = 1200),
            CartUiState.Issue.NotEnoughStock(availableQuantity = 2),
        )
    }

    @Test
    fun `item at the stock the validation reported can't grow`() {
        val cart = testCart(testCartItem(productId = "1", quantity = 2, availableQuantity = 7))
        val validation = CartValidation(issues = listOf(ItemIssue.NotEnoughStock("1", availableQuantity = 2)))

        val item = cart.toContent(validation).items.single()

        assertThat(item.canAddOneMore).isFalse()
    }

    @Test
    fun `changes count the items with each kind of pending change`() {
        val cart = testCart(
            testCartItem(productId = "1", price = 1000),
            testCartItem(productId = "2", price = 1000),
            testCartItem(productId = "3"),
        )
        val validation = CartValidation(
            issues = listOf(
                ItemIssue.PriceChanged("1", newPrice = 1200),
                ItemIssue.PriceChanged("2", newPrice = 1100),
                ItemIssue.Unavailable("3"),
            ),
        )

        val changes = cart.toContent(validation).changes

        assertThat(changes).isEqualTo(
            CartUiState.Changes(priceChangeCount = 2, unavailableItemCount = 1, notEnoughStockItemCount = 0),
        )
    }

    @Test
    fun `working promo code gives a discount line`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        val totals = cart.toContent(CartValidation()).totals

        assertThat(totals).isEqualTo(CartUiState.Totals(subtotal = 2000, discount = 200, total = 1800))
    }

    @Test
    fun `invalid promo code is shown as invalid`() {
        val cart = testCart(testCartItem(), promoCode = PromoCode("SALE10", 10))

        val promoCode = cart.toContent(CartValidation(invalidPromoCode = "SALE10")).promoCode

        assertThat(promoCode).isNotNull().prop(CartUiState.AppliedPromoCode::isValid).isFalse()
    }

    @Test
    fun `invalid promo code gives no discount line`() {
        val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

        val totals = cart.toContent(CartValidation(invalidPromoCode = "SALE10")).totals

        assertThat(totals).isEqualTo(CartUiState.Totals(subtotal = 2000, discount = null, total = 2000))
    }

    @Test
    fun `cart without a promo code has no discount line`() {
        val cart = testCart(testCartItem(price = 2000))

        val totals = cart.toContent(CartValidation()).totals

        assertThat(totals.discount).isNull()
    }
}
