package krio.systemdesign.shoppingapp.server.dto

import kotlinx.serialization.Serializable

// Формат совпадает с PromoCodeDTO в приложении (feature/promo).
@Serializable
data class PromoCodeDTO(
    val code: String,
    val discountPercent: Int,
)
