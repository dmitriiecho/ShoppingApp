package krio.systemdesign.shoppingapp.feature.catalog.data.dto

import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductsPage
import kotlinx.serialization.Serializable

@Serializable
data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)

fun ProductsPageDTO.toDomain(): ProductsPage = ProductsPage(
    products = products.map { it.toDomain() },
    endReached = endReached,
)
