package krio.systemdesign.shoppingapp.feature.promo.impl.data.dto

import krio.systemdesign.shoppingapp.contract.PromoCodeDTO
import krio.systemdesign.shoppingapp.crossfeature.domain.model.PromoCode

internal fun PromoCodeDTO.toDomain(): PromoCode = PromoCode(
    code = code,
    discountPercent = discountPercent,
)
