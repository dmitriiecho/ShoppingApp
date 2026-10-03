package krio.systemdesign.shoppingapp.domain.model

data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    // In cents.
    val price: Long,
    val quantity: Int,
    // Stock when the item was added; may be stale, the actual stock comes from cart validation.
    val availableQuantity: Int,
)
