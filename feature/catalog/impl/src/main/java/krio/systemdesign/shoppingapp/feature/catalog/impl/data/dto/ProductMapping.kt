package krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto

import krio.systemdesign.shoppingapp.contract.ProductDTO
import krio.systemdesign.shoppingapp.contract.ProductsPageDTO
import krio.systemdesign.shoppingapp.crossfeature.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage

internal fun ProductDTO.toDomain(): Product = Product(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    description = description,
    availableQuantity = availableQuantity,
)

internal fun ProductsPageDTO.toDomain(): ProductsPage = ProductsPage(
    products = products.map { it.toDomain() },
    endReached = endReached,
)
