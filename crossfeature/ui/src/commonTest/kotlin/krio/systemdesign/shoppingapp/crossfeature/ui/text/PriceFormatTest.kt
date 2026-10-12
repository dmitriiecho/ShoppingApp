package krio.systemdesign.shoppingapp.crossfeature.ui.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class PriceFormatTest {

    @Test
    fun `price in cents is shown in dollars`() {
        assertThat(formatPrice(123456)).isEqualTo("$1,234.56")
    }

    @Test
    fun `cents below ten keep the leading zero`() {
        assertThat(formatPrice(1005)).isEqualTo("$10.05")
    }

    @Test
    fun `millions are grouped by thousands`() {
        assertThat(formatPrice(123456789)).isEqualTo("$1,234,567.89")
    }

    @Test
    fun `a price under a dollar`() {
        assertThat(formatPrice(99)).isEqualTo("$0.99")
    }

    @Test
    fun `a negative amount starts with a minus`() {
        assertThat(formatPrice(-1500)).isEqualTo("-$15.00")
    }
}
