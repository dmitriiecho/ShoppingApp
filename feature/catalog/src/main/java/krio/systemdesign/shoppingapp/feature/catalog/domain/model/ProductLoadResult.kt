package krio.systemdesign.shoppingapp.feature.catalog.domain.model

import krio.systemdesign.shoppingapp.domain.model.Product

sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult
    data class Error(val error: Throwable) : ProductLoadResult
}
