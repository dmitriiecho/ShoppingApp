package krio.systemdesign.shoppingapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.Product

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
    val availableQuantity: Int,
)

internal fun CartItemEntity.toDomain(): CartItem = CartItem(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    availableQuantity = availableQuantity,
)

internal fun Product.toCartItemEntity(quantity: Int): CartItemEntity = CartItemEntity(
    productId = id,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    availableQuantity = availableQuantity,
)
