package krio.systemdesign.shoppingapp.domain.repository

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun add(product: Product, quantity: Int): Result<Unit>

    suspend fun setQuantity(productId: String, quantity: Int): Result<Unit>

    suspend fun remove(productId: String): Result<Unit>

    suspend fun clear(): Result<Unit>

    fun observe(): Flow<Cart>

    suspend fun validate(): CartValidationResult

    suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit>
}
