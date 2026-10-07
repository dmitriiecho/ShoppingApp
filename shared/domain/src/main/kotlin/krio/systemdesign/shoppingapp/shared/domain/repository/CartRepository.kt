package krio.systemdesign.shoppingapp.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

interface CartRepository {

    suspend fun addItem(
        product: Product,
        quantity: Int,
    ): Result<Unit>

    // quantity is above 0; UpdateCartQuantityUseCase turns 0 into removeItem.
    // A product that is no longer in the cart stays out of it: nothing is changed.
    suspend fun setQuantity(
        productId: String,
        quantity: Int,
    ): Result<Unit>

    suspend fun removeItem(productId: String): Result<Unit>

    // Removes the items but keeps the promo code.
    suspend fun clearItems(): Result<Unit>

    // Resets the cart to its initial state: removes items and the promo code at once.
    suspend fun reset(): Result<Unit>

    fun observe(): Flow<Cart>

    suspend fun validate(): CartValidationResult

    suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit>

    suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit>

    suspend fun removePromoCode(): Result<Unit>
}
