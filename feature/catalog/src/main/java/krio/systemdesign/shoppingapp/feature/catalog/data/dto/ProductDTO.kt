package krio.systemdesign.shoppingapp.feature.catalog.data.dto

import krio.systemdesign.shoppingapp.domain.model.Product
import kotlinx.serialization.Serializable

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

fun ProductDTO.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    description = description,
    availableQuantity = availableQuantity,
)
