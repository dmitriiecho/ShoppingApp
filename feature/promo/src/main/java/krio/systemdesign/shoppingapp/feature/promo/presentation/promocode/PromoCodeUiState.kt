package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import krio.systemdesign.shoppingapp.core.ui.text.UiText

data class PromoCodeUiState(
    val promoCode: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null,
) {
    val canApply: Boolean
        get() = promoCode.isNotBlank() && !isLoading
}
