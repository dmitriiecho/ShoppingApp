package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.model.PromoCode

data class PromoCodeUiState(
    val promoCode: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val availablePromoCodes: AvailablePromoCodes = AvailablePromoCodes.Loading,
) {
    val canApply: Boolean
        get() = promoCode.isNotBlank() && !isLoading
}

// Подсказка под полем ввода: какие коды есть на сервере. Приложение — тестовый стенд,
// и так не нужно помнить, что записано в server/data/promo-codes.json.
sealed interface AvailablePromoCodes {
    data object Loading : AvailablePromoCodes
    data class Content(val promoCodes: List<PromoCode>) : AvailablePromoCodes
    data object Error : AvailablePromoCodes
}
