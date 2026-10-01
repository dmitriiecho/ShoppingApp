package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.Serializable

// Формат совпадает с ProductDTO и ProductsPageDTO в приложении (feature/catalog).
@Serializable
data class ProductDTO(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val description: String,
    // false — товар закончился: он остаётся в каталоге, но заказать его нельзя.
    val available: Boolean,
)

@Serializable
data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)
