package krio.systemdesign.shoppingapp.data.api

import krio.systemdesign.shoppingapp.data.dto.AddCartItemRequest
import krio.systemdesign.shoppingapp.data.dto.CartDto
import krio.systemdesign.shoppingapp.data.dto.CartItemDto
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-process stand-in for a remote cart service, matching the fake catalog API pattern.
 * State lives in memory for the process lifetime and every call pays a network delay.
 */
internal class FakeCartApi : CartApi {

    private val mutex = Mutex()
    private val items = LinkedHashMap<String, CartItemDto>()

    override suspend fun getCart(): CartDto = withServer {
        snapshot()
    }

    override suspend fun addItem(request: AddCartItemRequest): CartDto = withServer {
        require(request.quantity > 0) { "quantity must be positive" }
        val existing = items[request.productId]
        items[request.productId] = existing?.copy(quantity = existing.quantity + request.quantity)
            ?: CartItemDto(
                productId = request.productId,
                name = request.name,
                imageUrl = request.imageUrl,
                price = request.price,
                quantity = request.quantity,
            )
        snapshot()
    }

    override suspend fun setQuantity(productId: String, quantity: Int): CartDto = withServer {
        if (quantity <= 0) {
            items.remove(productId)
            return@withServer snapshot()
        }
        val existing = items[productId]
            ?: error("Product $productId is not in the cart")
        items[productId] = existing.copy(quantity = quantity)
        snapshot()
    }

    override suspend fun removeItem(productId: String): CartDto = withServer {
        items.remove(productId)
        snapshot()
    }

    override suspend fun clear(): CartDto = withServer {
        items.clear()
        snapshot()
    }

    override suspend fun applyIssues(issues: List<ItemIssue>): CartDto = withServer {
        issues.forEach { issue ->
            when (issue) {
                is ItemIssue.Unavailable -> items.remove(issue.productId)
                is ItemIssue.PriceChanged -> {
                    val existing = items[issue.productId] ?: return@forEach
                    items[issue.productId] = existing.copy(price = issue.newPrice)
                }
            }
        }
        snapshot()
    }

    private suspend fun withServer(block: () -> CartDto): CartDto {
        delay(NETWORK_DELAY_MS)
        return mutex.withLock { block() }
    }

    private fun snapshot(): CartDto = CartDto(items = items.values.toList())

    private companion object {
        const val NETWORK_DELAY_MS = 300L
    }
}
