package krio.systemdesign.shoppingapp.domain.model

data class Product(
    val id: String,
    val name: String,
    // In cents: 14999 is $149.99.
    val price: Long,
    val imageUrl: String,
    val description: String,
    val availableQuantity: Int,
) {
    val isAvailable: Boolean get() = availableQuantity > 0
}
