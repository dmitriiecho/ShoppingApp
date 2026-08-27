package krio.systemdesign.shoppingapp.domain.model

data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
)
