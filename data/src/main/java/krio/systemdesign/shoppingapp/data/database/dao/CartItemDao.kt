package krio.systemdesign.shoppingapp.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import krio.systemdesign.shoppingapp.data.database.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items ORDER BY rowid ASC")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun find(productId: String): CartItemEntity?

    @Upsert
    suspend fun upsert(entity: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
}
