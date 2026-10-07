package krio.systemdesign.shoppingapp.shared.data.source

import androidx.room.withTransaction
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
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

internal class RoomLocalCartDataSource @Inject constructor(
    private val database: ShoppingDatabase,
    private val cartItemDao: CartItemDao,
    private val appliedPromoCodeDao: AppliedPromoCodeDao,
) : LocalCartDataSource {

    override suspend fun addItem(
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

    override suspend fun setQuantity(
        productId: String,
        quantity: Int,
    ): Result<Unit> = databaseCall {
        require(quantity > 0) { "quantity must be positive" }
        // Already removed, e.g. by a fast tap on Remove just before this "+": nothing to change.
        val existing = cartItemDao.find(productId) ?: return@databaseCall
        cartItemDao.upsert(existing.copy(quantity = quantity))
    }

    override suspend fun removeItem(productId: String): Result<Unit> = databaseCall {
        cartItemDao.delete(productId)
    }

    override suspend fun clearItems(): Result<Unit> = databaseCall {
        cartItemDao.deleteAll()
    }

    override suspend fun reset(): Result<Unit> = databaseCall {
        database.withTransaction {
            cartItemDao.deleteAll()
            appliedPromoCodeDao.delete()
        }
    }

    override fun observe(): Flow<Cart> = combine(
        cartItemDao.observeAll(),
        appliedPromoCodeDao.observe(),
    ) { entities, promoCode ->
        Cart(
            items = entities.map { it.toDomain() }.toPersistentList(),
            promoCode = promoCode?.toDomain(),
        )
    }

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = databaseCall {
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

    override suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit> = databaseCall {
        appliedPromoCodeDao.upsert(promoCode.toEntity())
    }

    override suspend fun removePromoCode(): Result<Unit> = databaseCall {
        appliedPromoCodeDao.delete()
    }
}
