package krio.systemdesign.shoppingapp.feature.promo.impl.data.dto

import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Serializable
internal data class PromoCodeDTO(
    val code: String,
    val discountPercent: Int,
)

internal fun PromoCodeDTO.toDomain(): PromoCode = PromoCode(
    code = code,
    discountPercent = discountPercent,
)
