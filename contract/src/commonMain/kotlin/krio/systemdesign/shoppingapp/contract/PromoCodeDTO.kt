package krio.systemdesign.shoppingapp.contract

import kotlinx.serialization.Serializable

// GET /promo-codes/{code}, and an item of GET /promo-codes.
@Serializable
data class PromoCodeDTO(
    val code: String,
    // 1 to 100; never changes once the code exists.
    val discountPercent: Int,
)
