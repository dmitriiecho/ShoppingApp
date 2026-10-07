package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.Serializable

// Same format as PromoCodeDTO in :feature:promo:impl.
@Serializable
data class PromoCodeDTO(
    val code: String,
    val discountPercent: Int,
)
