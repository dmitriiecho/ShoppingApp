package krio.systemdesign.shoppingapp.shared.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Long, // In US cents: 14999 is $149.99.
    val imageUrl: String,
    val description: String,
    val availableQuantity: Int,
) {
    val isAvailable: Boolean get() = availableQuantity > 0
}
