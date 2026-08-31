package krio.systemdesign.shoppingapp.data.dto

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartItem
import kotlinx.collections.immutable.toPersistentList

data class CartDto(
    val items: List<CartItemDto>,
)

data class CartItemDto(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
)

data class AddCartItemRequest(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
)

fun CartDto.toCart(): Cart = Cart(
    items = items.map { it.toCartItem() }.toPersistentList(),
)

private fun CartItemDto.toCartItem(): CartItem = CartItem(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
)
