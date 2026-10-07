package krio.systemdesign.shoppingapp.server.data

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
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

    // The app's settings put copies of these products in the cart to show one cart issue each (use cases in
    // feature/settings of the app, table in README.md). The numbers mirror those use cases.
    @Test
    fun `products the settings screen relies on keep their values`() {
        val products = ShopData.load(Path("data")).products.associateBy { it.id }
        fun product(id: String) = assertNotNull(products[id], "Product $id is gone")

        // Unavailable only.
        assertEquals(0, product("16").availableQuantity, "Hoodie (16) must be out of stock")

        // Not enough stock only: 10 are added at 4699.
        val basket = product("48")
        assertTrue(basket.availableQuantity in 1..9, "Laundry Basket (48) must have 1 to 9 in stock")
        assertEquals(4699, basket.price, "Laundry Basket (48) must cost 4699")

        // A changed price only: 1 is added at 4900.
        val board = product("40")
        assertTrue(board.availableQuantity > 0, "Cutting Board (40) must be in stock")
        assertNotEquals(4900, board.price, "Cutting Board (40) must not cost 4900")

        // Both a changed price and not enough stock: 10 are added at 995.
        val notebook = product("32")
        assertTrue(notebook.availableQuantity in 1..9, "Notebook (32) must have 1 to 9 in stock")
        assertNotEquals(995, notebook.price, "Notebook (32) must not cost 995")
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
