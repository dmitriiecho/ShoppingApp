package krio.systemdesign.shoppingapp.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import krio.systemdesign.shoppingapp.data.database.entity.AppliedPromoCodeEntity
import krio.systemdesign.shoppingapp.data.database.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY rowid ASC")
    fun observeItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun findItem(productId: String): CartItemEntity?

    @Upsert
    suspend fun upsertItem(entity: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM applied_promo_code LIMIT 1")
    fun observePromoCode(): Flow<AppliedPromoCodeEntity?>

    @Upsert
    suspend fun upsertPromoCode(entity: AppliedPromoCodeEntity)

    @Query("DELETE FROM applied_promo_code")
    suspend fun deletePromoCode()
}
