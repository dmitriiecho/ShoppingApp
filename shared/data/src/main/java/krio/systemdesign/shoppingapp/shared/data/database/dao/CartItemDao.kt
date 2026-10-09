package krio.systemdesign.shoppingapp.shared.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.data.database.entity.CartItemEntity

@Dao
internal interface CartItemDao {
    @Query("SELECT * FROM cart_items ORDER BY rowid ASC")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun find(productId: String): CartItemEntity?

    @Upsert
    suspend fun upsert(entity: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(
        productId: String,
        quantity: Int,
    )

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
}
