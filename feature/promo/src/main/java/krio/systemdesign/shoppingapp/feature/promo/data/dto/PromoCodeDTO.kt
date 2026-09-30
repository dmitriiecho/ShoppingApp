package krio.systemdesign.shoppingapp.feature.promo.data.dto

import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.serialization.Serializable

@Serializable
data class PromoCodeDTO(
    val code: String,
    val discountPercent: Int,
)

fun PromoCodeDTO.toDomain(): PromoCode = PromoCode(
    code = code,
    discountPercent = discountPercent,
)
