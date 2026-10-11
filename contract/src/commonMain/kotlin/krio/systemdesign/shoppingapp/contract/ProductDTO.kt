package krio.systemdesign.shoppingapp.contract

import kotlinx.serialization.Serializable

// GET /products/{id}, and an item of GET /products.
@Serializable
data class ProductDTO(
    val id: String,
    val name: String,
    // US cents: 14999 is $149.99.
    val price: Long,
    // The image's full address: PUBLIC_URL/images/<file>.
    val imageUrl: String,
    val description: String,
    // 0 means out of stock: the product stays in the catalog but can't be ordered.
    val availableQuantity: Int,
)

// GET /products: a page of the catalog, filtered by the search query.
@Serializable
data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)
