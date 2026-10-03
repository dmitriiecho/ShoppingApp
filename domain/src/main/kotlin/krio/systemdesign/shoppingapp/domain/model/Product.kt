package krio.systemdesign.shoppingapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val description: String,
    val availableQuantity: Int,
) {
    val isAvailable: Boolean get() = availableQuantity > 0
}
