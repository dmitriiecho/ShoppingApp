package krio.systemdesign.shoppingapp.domain.repository

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun addItem(product: Product, quantity: Int): Result<Unit>

    suspend fun setQuantity(productId: String, quantity: Int): Result<Unit>

    suspend fun removeItem(productId: String): Result<Unit>

    suspend fun clearItems(): Result<Unit>

    // Resets the cart to its initial state: removes items and the promo code at once.
    suspend fun reset(): Result<Unit>

    fun observe(): Flow<Cart>

    suspend fun validate(): CartValidationResult

    suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit>

    suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit>

    suspend fun removePromoCode(): Result<Unit>
}
