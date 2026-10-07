package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.Serializable

// Same format as ProductDTO and ProductsPageDTO in :feature:catalog:impl.
@Serializable
data class ProductDTO(
    val id: String,
    val name: String,
    // US cents: 14999 is $149.99.
    val price: Long,
    val imageUrl: String,
    val description: String,
    // 0 means out of stock: the product stays in the catalog but can't be ordered.
    val availableQuantity: Int,
)

@Serializable
data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)
