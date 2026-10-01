package krio.systemdesign.shoppingapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val description: String,
    // Сколько штук можно заказать. 0 — товар закончился.
    val availableQuantity: Int,
) {
    val isAvailable: Boolean get() = availableQuantity > 0
}
