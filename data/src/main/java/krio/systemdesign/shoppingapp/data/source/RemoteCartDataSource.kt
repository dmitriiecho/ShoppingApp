package krio.systemdesign.shoppingapp.data.source

import krio.systemdesign.shoppingapp.data.api.CartApi
import krio.systemdesign.shoppingapp.data.dto.AddCartItemRequest
import krio.systemdesign.shoppingapp.data.dto.toCart
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Remote-first cart: the API is the source of truth, a memory cache drives [observe].
 * Unlike [RoomLocalCartDataSource], the cart does not survive process death
 * (a real backend would persist it server-side).
 */
class RemoteCartDataSource @Inject constructor(
    private val api: CartApi,
) : LocalCartDataSource {

    private val mutex = Mutex()
    private val cart = MutableStateFlow(Cart(persistentListOf()))
    private var loaded = false

    override suspend fun add(product: Product, quantity: Int): Result<Unit> = runCatching {
        require(quantity > 0) { "quantity must be positive" }
        ensureLoaded()
        cart.value = api.addItem(
            AddCartItemRequest(
                productId = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price,
                quantity = quantity,
            ),
        ).toCart()
    }

    override suspend fun setQuantity(productId: String, quantity: Int): Result<Unit> = runCatching {
        ensureLoaded()
        cart.value = api.setQuantity(productId, quantity).toCart()
    }

    override suspend fun remove(productId: String): Result<Unit> = runCatching {
        ensureLoaded()
        cart.value = api.removeItem(productId).toCart()
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        ensureLoaded()
        cart.value = api.clear().toCart()
    }

    override fun observe(): Flow<Cart> = cart
        .asStateFlow()
        .onStart { ensureLoaded() }

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = runCatching {
        ensureLoaded()
        cart.value = api.applyIssues(issues).toCart()
    }

    private suspend fun ensureLoaded() {
        mutex.withLock {
            if (!loaded) {
                cart.value = api.getCart().toCart()
                loaded = true
            }
        }
    }
}
