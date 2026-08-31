package krio.systemdesign.shoppingapp.data.api

import krio.systemdesign.shoppingapp.data.dto.AddCartItemRequest
import krio.systemdesign.shoppingapp.data.dto.CartDto
import krio.systemdesign.shoppingapp.domain.model.ItemIssue

/**
 * Server-side cart API. The backend is the source of truth;
 * each mutation returns the updated cart snapshot.
 */
interface CartApi {

    suspend fun getCart(): CartDto

    suspend fun addItem(request: AddCartItemRequest): CartDto

    suspend fun setQuantity(productId: String, quantity: Int): CartDto

    suspend fun removeItem(productId: String): CartDto

    suspend fun clear(): CartDto

    suspend fun applyIssues(issues: List<ItemIssue>): CartDto
}
