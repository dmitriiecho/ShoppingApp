package krio.systemdesign.shoppingapp.data.source

import androidx.room.withTransaction
import krio.systemdesign.shoppingapp.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.data.database.dao.CartDao
import krio.systemdesign.shoppingapp.data.database.entity.CartItemEntity
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLocalCartDataSource @Inject constructor(
    private val database: ShoppingDatabase,
    private val cartDao: CartDao,
) : LocalCartDataSource {

    override suspend fun add(product: Product, quantity: Int): Result<Unit> = runCatching {
        require(quantity > 0) { "quantity must be positive" }
        database.withTransaction {
            val existing = cartDao.find(product.id)
            val entity = existing?.copy(quantity = existing.quantity + quantity)
                ?: CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    imageUrl = product.imageUrl,
                    price = product.price,
                    quantity = quantity,
                )
            cartDao.upsert(entity)
        }
    }

    override suspend fun setQuantity(productId: String, quantity: Int): Result<Unit> = runCatching {
        if (quantity <= 0) {
            cartDao.delete(productId)
            return@runCatching
        }
        val existing = cartDao.find(productId)
            ?: error("Product $productId is not in the cart")
        cartDao.upsert(existing.copy(quantity = quantity))
    }

    override suspend fun remove(productId: String): Result<Unit> = runCatching {
        cartDao.delete(productId)
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        cartDao.deleteAll()
    }

    override fun observe(): Flow<Cart> {
        return cartDao.observeAll().map { entities ->
            Cart(items = entities.map { it.toCartItem() }.toPersistentList())
        }
    }

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = runCatching {
        database.withTransaction {
            issues.forEach { issue ->
                when (issue) {
                    is ItemIssue.Unavailable -> cartDao.delete(issue.productId)
                    is ItemIssue.PriceChanged -> {
                        val existing = cartDao.find(issue.productId) ?: return@forEach
                        cartDao.upsert(existing.copy(price = issue.newPrice))
                    }
                }
            }
        }
    }
}

private fun CartItemEntity.toCartItem(): CartItem = CartItem(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
)
