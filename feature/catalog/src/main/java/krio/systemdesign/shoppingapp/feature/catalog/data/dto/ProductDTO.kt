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
)

fun ProductDTO.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    description = description,
)
