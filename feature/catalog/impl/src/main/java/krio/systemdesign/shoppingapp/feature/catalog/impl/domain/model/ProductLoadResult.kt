package krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model

import krio.systemdesign.shoppingapp.shared.domain.model.Product

sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult
    data class Error(val error: Throwable) : ProductLoadResult
}
