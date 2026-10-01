package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.Serializable

// Формат совпадает с ProductDTO и ProductsPageDTO в приложении (feature/catalog).
@Serializable
data class ProductDTO(
    val id: String,
    val name: String,
    // Цена в центах: 14999 — это $149.99. Все цены в долларах США.
    val price: Long,
    val imageUrl: String,
    val description: String,
    // Сколько штук можно заказать. 0 — товар закончился: он остаётся в каталоге, но заказать его нельзя.
    val availableQuantity: Int,
)

@Serializable
data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)
