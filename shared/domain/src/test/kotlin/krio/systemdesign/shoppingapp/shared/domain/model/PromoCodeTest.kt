package krio.systemdesign.shoppingapp.shared.domain.model

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.tableOf
import kotlin.test.Test

class PromoCodeTest {

    @Test
    fun `discount is the percent of the subtotal`() {
        val discount = PromoCode("SALE10", 10).discountFor(2000)

        assertThat(discount).isEqualTo(200)
    }

    // Prices are whole cents, so the discount is rounded down.
    @Test
    fun `fraction of a cent in the discount is dropped`() {
        val discount = PromoCode("SALE15", 15).discountFor(999)

        assertThat(discount).isEqualTo(149)
    }

    @Test
    fun `promo code with a percent outside 1 to 100 is rejected`() {
        tableOf("discountPercent")
            .row(0)
            .row(101)
            .forAll { percent ->
                assertFailure { PromoCode("SALE", percent) }.isInstanceOf<IllegalArgumentException>()
            }
    }
}
