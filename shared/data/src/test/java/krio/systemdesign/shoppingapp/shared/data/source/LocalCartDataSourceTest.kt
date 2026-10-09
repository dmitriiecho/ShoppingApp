package krio.systemdesign.shoppingapp.shared.data.source

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import krio.systemdesign.shoppingapp.shared.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.shared.data.database.databaseTest
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testProduct
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalCartDataSourceTest {

    @Test
    fun `adding a product already in the cart adds to its quantity`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 2)

        cart.addItem(testProduct(id = "1"), quantity = 3)

        assertThat(cart.current().quantityOf("1")).isEqualTo(5)
    }

    // The catalog stock is fresher than the one saved when the item was added first.
    @Test
    fun `adding a product already in the cart takes the catalog's stock`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1", availableQuantity = 10), quantity = 1)

        cart.addItem(testProduct(id = "1", availableQuantity = 4), quantity = 1)

        assertThat(cart.current().items.single().availableQuantity).isEqualTo(4)
    }

    @Test
    fun `new quantity replaces the old one`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 2)

        cart.setQuantity("1", quantity = 5)

        assertThat(cart.current().quantityOf("1")).isEqualTo(5)
    }

    // A fast tap on "+" right after Remove must not bring the item back.
    @Test
    fun `new quantity of a removed item doesn't bring it back`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 1)
        cart.removeItem("1")

        cart.setQuantity("1", quantity = 2)

        assertThat(cart.current().items).isEmpty()
    }

    @Test
    fun `items come in the order they were added`() = databaseTest { database ->
        val cart = cartOf(database)

        cart.addItem(testProduct(id = "2"), quantity = 1)
        cart.addItem(testProduct(id = "1"), quantity = 1)

        assertThat(cart.current().items.map { it.productId }).containsExactly("2", "1")
    }

    @Test
    fun `accepting changes removes the unavailable items`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 1)
        cart.addItem(testProduct(id = "2"), quantity = 1)

        cart.acceptChanges(listOf(ItemIssue.Unavailable("1")))

        assertThat(cart.current().items.map { it.productId }).containsExactly("2")
    }

    @Test
    fun `accepting changes takes the new prices`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1", price = 1000), quantity = 1)

        cart.acceptChanges(listOf(ItemIssue.PriceChanged("1", newPrice = 1200)))

        assertThat(cart.current().items.single().price).isEqualTo(1200)
    }

    // The user lowers each such item's quantity themselves.
    @Test
    fun `accepting changes leaves the items short of stock as they are`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 3)

        cart.acceptChanges(listOf(ItemIssue.NotEnoughStock("1", availableQuantity = 2)))

        assertThat(cart.current().quantityOf("1")).isEqualTo(3)
    }

    @Test
    fun `clearing the items keeps the promo code`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 1)
        cart.applyPromoCode(PromoCode("SALE10", 10))

        cart.clearItems()

        assertThat(cart.current()).isEqualTo(Cart(items = emptyList(), promoCode = PromoCode("SALE10", 10)))
    }

    @Test
    fun `reset empties the cart and removes the promo code`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 1)
        cart.applyPromoCode(PromoCode("SALE10", 10))

        cart.reset()

        assertThat(cart.current()).isEqualTo(Cart(items = emptyList(), promoCode = null))
    }

    @Test
    fun `applied promo code replaces the previous one`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.applyPromoCode(PromoCode("SALE10", 10))

        cart.applyPromoCode(PromoCode("SALE25", 25))

        assertThat(cart.current().promoCode).isEqualTo(PromoCode("SALE25", 25))
    }

    @Test
    fun `removed promo code is gone`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.applyPromoCode(PromoCode("SALE10", 10))

        cart.removePromoCode()

        assertThat(cart.current().promoCode).isNull()
    }

    // Only a database failure becomes Result.failure; a wrong quantity is a bug in the caller and must crash.
    @Test
    fun `adding zero of a product is a bug, not a database error`() = databaseTest { database ->
        val cart = cartOf(database)

        assertFailure { cart.addItem(testProduct(id = "1"), quantity = 0) }.isInstanceOf<IllegalArgumentException>()
    }

    private fun cartOf(database: ShoppingDatabase) =
        LocalCartDataSource(database, database.cartItemDao(), database.appliedPromoCodeDao())

    private suspend fun LocalCartDataSource.current(): Cart = observe().first()
}
