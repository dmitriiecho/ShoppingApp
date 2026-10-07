package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

import androidx.compose.foundation.text.input.TextFieldState
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

internal data class PromoCodeUiState(
    // A state holder, not a String: the field edits it in place, so typing never waits for this flow
    // and can't lose characters. It's the same instance for the whole screen.
    val promoCode: TextFieldState,
    val check: Check = Check.Idle,
    val availableCodes: AvailableCodes = AvailableCodes.Loading,
) {
    val isChecking: Boolean
        get() = check == Check.Checking

    val checkError: UiText?
        get() = (check as? Check.Failed)?.error

    val canApply: Boolean
        get() = promoCode.text.isNotBlank() && !isChecking

    // The check of the entered code. No success state: on success the screen closes with the code.
    sealed interface Check {
        data object Idle : Check
        data object Checking : Check
        data class Failed(val error: UiText) : Check
    }

    // The server's codes, shown under the field: the app is a test stand, so nobody has to remember
    // what server/data/promo-codes.json holds.
    sealed interface AvailableCodes {
        data object Loading : AvailableCodes
        data class Loaded(val promoCodes: List<PromoCode>) : AvailableCodes
        data object Error : AvailableCodes
    }
}
