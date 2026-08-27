package krio.systemdesign.shoppingapp.data.source

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface LocalCartDataSource {

    suspend fun add(product: Product, quantity: Int): Result<Unit>

    suspend fun setQuantity(productId: String, quantity: Int): Result<Unit>

    suspend fun remove(productId: String): Result<Unit>

    suspend fun clear(): Result<Unit>

    fun observe(): Flow<Cart>

    suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit>
}
