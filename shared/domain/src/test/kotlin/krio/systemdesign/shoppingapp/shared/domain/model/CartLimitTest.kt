package krio.systemdesign.shoppingapp.shared.domain.model

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class CartLimitTest {

    @Test
    fun `one more can be added while the cart holds less than the stock`() {
        assertThat(canAddOneMore(inCart = 2, stock = 3)).isTrue()
    }

    @Test
    fun `nothing more can be added once the cart holds the whole stock`() {
        assertThat(canAddOneMore(inCart = 3, stock = 3)).isFalse()
    }
}
