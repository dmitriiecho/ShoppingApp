package krio.systemdesign.shoppingapp.shared.data.source

import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult

internal interface CartValidatorDataSource {

    suspend fun validate(cart: Cart): CartValidationResult
}
