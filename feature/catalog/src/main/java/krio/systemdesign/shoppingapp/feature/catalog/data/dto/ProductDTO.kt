package krio.systemdesign.shoppingapp.feature.catalog.data.dto

import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.serialization.Serializable

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

fun ProductDTO.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    description = description,
    isAvailable = available,
)
