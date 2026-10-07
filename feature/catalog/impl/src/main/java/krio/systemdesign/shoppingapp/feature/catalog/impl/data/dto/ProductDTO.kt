package krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto

import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.shared.domain.model.Product

@Serializable
internal data class ProductDTO(
    val id: String,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val description: String,
    val availableQuantity: Int,
)

internal fun ProductDTO.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    description = description,
    availableQuantity = availableQuantity,
)
