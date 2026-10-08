package krio.systemdesign.shoppingapp.server.data

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import assertk.tableOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import krio.systemdesign.shoppingapp.server.testProduct
import krio.systemdesign.shoppingapp.server.testShopData

class ShopDataTest {

    @Test
    fun `two products with one id are rejected`() {
        assertFailure { testShopData(products = listOf(testProduct(id = "1"), testProduct(id = "1"))) }
            .isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `product with negative stock is rejected`() {
        assertFailure { testShopData(products = listOf(testProduct(availableQuantity = -1))) }
            .isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `promo codes differing only in case are rejected`() {
        assertFailure { testShopData(promoCodes = listOf(PromoCodeDTO("SALE10", 10), PromoCodeDTO("sale10", 20))) }
            .isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `promo code percent outside 1 to 100 is rejected`() {
        tableOf("discountPercent")
            .row(0)
            .row(101)
            .forAll { percent ->
                assertFailure { testShopData(promoCodes = listOf(PromoCodeDTO("SALE", percent))) }
                    .isInstanceOf<IllegalArgumentException>()
            }
    }
}
