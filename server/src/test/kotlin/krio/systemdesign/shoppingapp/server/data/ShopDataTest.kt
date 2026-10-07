package krio.systemdesign.shoppingapp.server.data

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import krio.systemdesign.shoppingapp.server.TEST_DATA
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import krio.systemdesign.shoppingapp.server.testProduct

class ShopDataTest {

    @Test
    fun `files in data are read`() {
        val data = ShopData.load(Path("data"))
        assertTrue(data.products.isNotEmpty())
        assertTrue(data.promoCodes.isNotEmpty())
    }

    @Test
    fun `invalid data is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            ShopData(products = TEST_DATA.products + TEST_DATA.products[0], promoCodes = emptyList())
        }
        assertFailsWith<IllegalArgumentException> {
            ShopData(
                products = listOf(testProduct(id = "1", name = "Mug", availableQuantity = -1)),
                promoCodes = emptyList(),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ShopData(products = emptyList(), promoCodes = listOf(PromoCodeDTO("FREE", 0)))
        }
        assertFailsWith<IllegalArgumentException> {
            ShopData(
                products = emptyList(),
                promoCodes = listOf(PromoCodeDTO("SALE10", 10), PromoCodeDTO("sale10", 20)),
            )
        }
    }
}
