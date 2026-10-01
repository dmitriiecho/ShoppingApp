package krio.systemdesign.shoppingapp.domain.model

data class Product(
    val id: String,
    val name: String,
    // Цена в центах: 14999 — это $149.99. Все цены в приложении и на сервере в долларах США.
    val price: Long,
    val imageUrl: String,
    val description: String,
    // Сколько штук можно заказать. 0 — товар закончился.
    val availableQuantity: Int,
) {
    val isAvailable: Boolean get() = availableQuantity > 0
}
