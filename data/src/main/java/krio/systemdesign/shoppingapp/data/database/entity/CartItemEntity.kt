package krio.systemdesign.shoppingapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
)
