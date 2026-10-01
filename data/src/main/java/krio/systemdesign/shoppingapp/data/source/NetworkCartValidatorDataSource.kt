package krio.systemdesign.shoppingapp.data.source

import krio.systemdesign.shoppingapp.core.network.NetworkResult
import krio.systemdesign.shoppingapp.core.network.networkCall
import krio.systemdesign.shoppingapp.data.api.CartApi
import krio.systemdesign.shoppingapp.data.dto.toDomain
import krio.systemdesign.shoppingapp.data.dto.toValidationRequest
import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import javax.inject.Inject

internal class NetworkCartValidatorDataSource @Inject constructor(
    private val api: CartApi,
) : CartValidatorDataSource {
    override suspend fun validate(cart: Cart): CartValidationResult {
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
