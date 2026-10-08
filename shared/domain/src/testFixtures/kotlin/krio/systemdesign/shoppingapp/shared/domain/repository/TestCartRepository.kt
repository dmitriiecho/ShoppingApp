package krio.systemdesign.shoppingapp.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartItem
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testCart

// A cart in memory that changes the way the app's cart does. The test decides what the server answers and can
// make every change fail, as with a database error.
class TestCartRepository(initialCart: Cart = testCart()) : CartRepository {
    private val cart = MutableStateFlow(initialCart)

    val currentCart: Cart get() = cart.value

    // What validate() answers for the cart sent. A test can suspend in it to change the cart during the request.
    var validationAnswer: suspend (Cart) -> CartValidationResult = { CartValidationResult.Success }

    var changeError: Throwable? = null

    override suspend fun addItem(
        product: Product,
        quantity: Int,
    ): Result<Unit> = change { cart ->
        val existing = cart.items.find { it.productId == product.id }
        if (existing == null) {
            cart.copy(items = cart.items + product.toCartItem(quantity))
        } else {
            // As in the app: the catalog stock is fresher than the stored one.
            val updated = existing.copy(
                quantity = existing.quantity + quantity,
                availableQuantity = product.availableQuantity,
            )
            cart.copy(items = cart.items.map { if (it == existing) updated else it })
        }
    }

    override suspend fun setQuantity(
        productId: String,
        quantity: Int,
    ): Result<Unit> = change { cart ->
        cart.copy(items = cart.items.map { if (it.productId == productId) it.copy(quantity = quantity) else it })
    }

    override suspend fun removeItem(productId: String): Result<Unit> = change { cart ->
        cart.copy(items = cart.items.filterNot { it.productId == productId })
    }

    override suspend fun clearItems(): Result<Unit> = change { it.copy(items = emptyList()) }

    override suspend fun reset(): Result<Unit> = change { testCart() }

    override fun observe(): Flow<Cart> = cart

    override suspend fun validate(): CartValidationResult = validationAnswer(cart.value)

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = change { cart ->
        val unavailable = issues.filterIsInstance<ItemIssue.Unavailable>().map { it.productId }.toSet()
        val newPrices = issues.filterIsInstance<ItemIssue.PriceChanged>().associate { it.productId to it.newPrice }
        cart.copy(
            items = cart.items
                .filterNot { it.productId in unavailable }
                .map { item -> newPrices[item.productId]?.let { item.copy(price = it) } ?: item },
        )
    }

    override suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit> = change { it.copy(promoCode = promoCode) }

    override suspend fun removePromoCode(): Result<Unit> = change { it.copy(promoCode = null) }

    private fun Product.toCartItem(quantity: Int) = CartItem(
        productId = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
        quantity = quantity,
        availableQuantity = availableQuantity,
    )

    private fun change(update: (Cart) -> Cart): Result<Unit> {
        changeError?.let { return Result.failure(it) }
        cart.update(update)
        return Result.success(Unit)
    }
}
