package krio.systemdesign.shoppingapp.shared.data.source

import javax.inject.Inject
import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.shared.data.api.CartApi
import krio.systemdesign.shoppingapp.shared.data.dto.toDomain
import krio.systemdesign.shoppingapp.shared.data.dto.toValidationRequest
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult

internal class CartValidatorDataSource @Inject constructor(private val api: CartApi) {
    suspend fun validate(cart: Cart): CartValidationResult {
        val result = networkCall { api.validate(cart.toValidationRequest()) }
        return when (result) {
            is NetworkResult.Success -> {
                val body = result.body
                if (body.issues.isEmpty() && body.promoCodeValid) {
                    CartValidationResult.Success
                } else {
                    CartValidationResult.Invalid(
                        issues = body.issues.map { it.toDomain() },
                        isPromoCodeValid = body.promoCodeValid,
                    )
                }
            }
            is NetworkResult.HttpError -> CartValidationResult.Error(result.error)
            is NetworkResult.Failure -> CartValidationResult.Error(result.error)
        }
    }
}
