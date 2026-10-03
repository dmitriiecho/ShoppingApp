package krio.systemdesign.shoppingapp.data.source

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult

internal interface CartValidatorDataSource {

    suspend fun validate(cart: Cart): CartValidationResult
}
