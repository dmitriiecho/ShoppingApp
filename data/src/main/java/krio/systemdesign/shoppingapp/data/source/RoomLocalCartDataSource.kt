package krio.systemdesign.shoppingapp.data.source

import androidx.room.withTransaction
import krio.systemdesign.shoppingapp.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.data.database.dao.CartDao
import krio.systemdesign.shoppingapp.data.database.entity.AppliedPromoCodeEntity
import krio.systemdesign.shoppingapp.data.database.entity.CartItemEntity
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RoomLocalCartDataSource @Inject constructor(
    private val database: ShoppingDatabase,
    private val cartDao: CartDao,
) : LocalCartDataSource {

    override suspend fun add(product: Product, quantity: Int): Result<Unit> = runCatching {
        require(quantity > 0) { "quantity must be positive" }
        database.withTransaction {
            val existing = cartDao.findItem(product.id)
            val entity = existing?.copy(quantity = existing.quantity + quantity)
                ?: CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    imageUrl = product.imageUrl,
                    price = product.price,
                    quantity = quantity,
                )
            cartDao.upsertItem(entity)
        }
    }

    override suspend fun setQuantity(productId: String, quantity: Int): Result<Unit> = runCatching {
        if (quantity <= 0) {
            cartDao.deleteItem(productId)
            return@runCatching
        }
        val existing = cartDao.findItem(productId)
            ?: error("Product $productId is not in the cart")
        cartDao.upsertItem(existing.copy(quantity = quantity))
    }

    override suspend fun remove(productId: String): Result<Unit> = runCatching {
        cartDao.deleteItem(productId)
    }

    override suspend fun clear(): Result<Unit> = runCatching {
        database.withTransaction {
            cartDao.deleteAllItems()
            cartDao.deletePromoCode()
        }
    }

    override fun observe(): Flow<Cart> {
        return combine(
            cartDao.observeItems(),
            cartDao.observePromoCode(),
        ) { entities, promoCode ->
            Cart(
                items = entities.map { it.toCartItem() }.toPersistentList(),
                promoCode = promoCode?.toPromoCode(),
            )
        }
    }

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> = runCatching {
        database.withTransaction {
            issues.forEach { issue ->
                when (issue) {
                    is ItemIssue.Unavailable -> cartDao.deleteItem(issue.productId)
                    is ItemIssue.PriceChanged -> {
                        val existing = cartDao.findItem(issue.productId) ?: return@forEach
                        cartDao.upsertItem(existing.copy(price = issue.newPrice))
                    }
                }
            }
        }
    }

    override suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit> = runCatching {
        cartDao.upsertPromoCode(
            AppliedPromoCodeEntity(
                code = promoCode.code,
                discountPercent = promoCode.discountPercent,
            ),
        )
    }

    override suspend fun removePromoCode(): Result<Unit> = runCatching {
        cartDao.deletePromoCode()
    }
}

private fun CartItemEntity.toCartItem(): CartItem = CartItem(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
)

private fun AppliedPromoCodeEntity.toPromoCode(): PromoCode = PromoCode(
    code = code,
    discountPercent = discountPercent,
)
