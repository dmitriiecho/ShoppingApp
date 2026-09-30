package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

data class PromoCodeUiState(
    val promoCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val canApply: Boolean
        get() = promoCode.isNotBlank() && !isLoading
}
