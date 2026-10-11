package krio.systemdesign.shoppingapp.shared.data.source

import androidx.room.withTransaction
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import krio.systemdesign.shoppingapp.shared.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.shared.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.shared.data.database.dao.CartItemDao
import krio.systemdesign.shoppingapp.shared.data.database.databaseCall
import krio.systemdesign.shoppingapp.shared.data.database.entity.toCartItemEntity
import krio.systemdesign.shoppingapp.shared.data.database.entity.toDomain
import krio.systemdesign.shoppingapp.shared.data.database.entity.toEntity
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Inject
internal class LocalCartDataSource(
    private val database: ShoppingDatabase,
    private val cartItemDao: CartItemDao,
    private val appliedPromoCodeDao: AppliedPromoCodeDao,
) {

    suspend fun addItem(
        product: Product,
        quantity: Int,
    ): Result<Unit> = databaseCall {
        require(quantity > 0) { "quantity must be positive" }
        database.withTransaction {
            val existing = cartItemDao.find(product.id)
            val entity = if (existing != null) {
                // The catalog stock is fresher than the stored one.
                existing.copy(
                    quantity = existing.quantity + quantity,
                    availableQuantity = product.availableQuantity,
                )
            } else {
                product.toCartItemEntity(quantity)
            }
            cartItemDao.upsert(entity)
        }
    }

    suspend fun setQuantity(
        productId: String,
        quantity: Int,
    ): Result<Unit> = databaseCall {
        require(quantity > 0) { "quantity must be positive" }
        // One statement, so a fast tap on Remove can't slip in between a read and a write and the item stays
        // removed: an UPDATE of a removed item changes nothing.
        cartItemDao.updateQuantity(productId, quantity)
    }

    suspend fun removeItem(productId: String): Result<Unit> = databaseCall {
        cartItemDao.delete(productId)
    }

    suspend fun clearItems(): Result<Unit> = databaseCall {
        cartItemDao.deleteAll()
    }

    suspend fun reset(): Result<Unit> = databaseCall {
        database.withTransaction {
            cartItemDao.deleteAll()
            appliedPromoCodeDao.delete()
        }
    }

    fun observe(): Flow<Cart> = combine(
        cartItemDao.observeAll(),
        appliedPromoCodeDao.observe(),
    ) { entities, promoCode ->
        Cart(
            items = entities.map { it.toDomain() },
            promoCode = promoCode?.toDomain(),
        )
    }

    suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = databaseCall {
        database.withTransaction {
            issues.forEach { issue ->
                when (issue) {
                    is ItemIssue.Unavailable -> cartItemDao.delete(issue.productId)
                    is ItemIssue.PriceChanged -> {
                        val existing = cartItemDao.find(issue.productId) ?: return@forEach
                        cartItemDao.upsert(existing.copy(price = issue.newPrice))
                    }
                    // The user decreases the quantity themselves for each item; such changes are not accepted in bulk.
                    is ItemIssue.NotEnoughStock -> Unit
                }
            }
        }
    }

    suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit> = databaseCall {
        appliedPromoCodeDao.upsert(promoCode.toEntity())
    }

    suspend fun removePromoCode(): Result<Unit> = databaseCall {
        appliedPromoCodeDao.delete()
    }
}
