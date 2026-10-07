package krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto

import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage

@Serializable
internal data class ProductsPageDTO(
    val products: List<ProductDTO>,
    val endReached: Boolean,
)

internal fun ProductsPageDTO.toDomain(): ProductsPage = ProductsPage(
    products = products.map { it.toDomain() },
    endReached = endReached,
)
