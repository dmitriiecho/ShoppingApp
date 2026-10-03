package krio.systemdesign.shoppingapp.data.repository

import krio.systemdesign.shoppingapp.data.source.CartValidatorDataSource
import krio.systemdesign.shoppingapp.data.source.LocalCartDataSource
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class CartRepositoryImpl @Inject constructor(
    private val localCart: LocalCartDataSource,
    private val validator: CartValidatorDataSource,
) : CartRepository {

    override suspend fun addItem(product: Product, quantity: Int): Result<Unit> =
        localCart.addItem(product, quantity)

    override suspend fun setQuantity(productId: String, quantity: Int): Result<Unit> =
        localCart.setQuantity(productId, quantity)

    override suspend fun removeItem(productId: String): Result<Unit> =
        localCart.removeItem(productId)

    override suspend fun clearItems(): Result<Unit> =
        localCart.clearItems()

    override suspend fun reset(): Result<Unit> =
        localCart.reset()

    override fun observe(): Flow<Cart> =
        localCart.observe()

    override suspend fun validate(): CartValidationResult {
        val cart = localCart.observe().first()
        return validator.validate(cart)
    }

    override suspend fun acceptChanges(issues: List<ItemIssue>): Result<Unit> =
        localCart.acceptChanges(issues)

    override suspend fun applyPromoCode(promoCode: PromoCode): Result<Unit> =
        localCart.applyPromoCode(promoCode)

    override suspend fun removePromoCode(): Result<Unit> =
        localCart.removePromoCode()
}
