package krio.systemdesign.shoppingapp.server.data

import kotlinx.serialization.Serializable

// A product as data/products.json holds it. The API sends it as ProductDTO, with the image's full address.
@Serializable
data class Product(
    val id: String,
    val name: String,
    // US cents: 14999 is $149.99.
    val price: Long,
    // A file in data/images/. Its address depends on where the server runs (PUBLIC_URL), so it isn't stored.
    val image: String,
    val description: String,
    // 0 means out of stock: the product stays in the catalog but can't be ordered.
    val availableQuantity: Int,
)
