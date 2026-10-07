package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.state.savedTextField
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.feature.promo.impl.R
import krio.systemdesign.shoppingapp.feature.promo.impl.analytics.PromoCodeRejectedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase.CheckPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase.GetPromoCodesUseCase
import krio.systemdesign.shoppingapp.shared.analytics.Analytics

@HiltViewModel
internal class PromoCodeViewModel @Inject constructor(
    private val checkPromoCode: CheckPromoCodeUseCase,
    private val getPromoCodes: GetPromoCodesUseCase,
    private val analytics: Analytics,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The code survives process death; the check and the list don't, since their requests die with the process.
    private val promoCode: TextFieldState = savedStateHandle.savedTextField(KEY_PROMO_CODE)
    private val check = MutableStateFlow<PromoCodeUiState.Check>(PromoCodeUiState.Check.Idle)
    private val availableCodes: MutableStateFlow<PromoCodeUiState.AvailableCodes> =
        MutableStateFlow(PromoCodeUiState.AvailableCodes.Loading)

    val uiState: StateFlow<PromoCodeUiState> = combine(
        check,
        availableCodes,
    ) { latestCheck, latestCodes ->
        PromoCodeUiState(
            promoCode = promoCode,
            check = latestCheck,
            availableCodes = latestCodes,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PromoCodeUiState(promoCode = promoCode),
    )

    private val _effects = Channel<PromoCodeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadAvailableCodes()
        clearCheckErrorOnEdit()
    }

    fun onEvent(event: PromoCodeEvent) {
        when (event) {
            PromoCodeEvent.OnApplyClick -> applyPromoCode()
            is PromoCodeEvent.OnAvailablePromoCodeClick -> pickPromoCode(event.code)
            PromoCodeEvent.OnRetryAvailableCodesClick -> loadAvailableCodes()
            PromoCodeEvent.OnBackClick -> send(PromoCodeEffect.NavigateBack)
        }
    }

    private fun loadAvailableCodes() {
        viewModelScope.launch {
            availableCodes.value = PromoCodeUiState.AvailableCodes.Loading
            availableCodes.value = getPromoCodes().fold(
                onSuccess = { PromoCodeUiState.AvailableCodes.Loaded(it) },
                onFailure = { PromoCodeUiState.AvailableCodes.Error },
            )
        }
    }

    // The field is locked while a code is checked, so the hint doesn't change it either.
    private fun pickPromoCode(code: String) {
        if (check.value == PromoCodeUiState.Check.Checking) return
        promoCode.setTextAndPlaceCursorAtEnd(code)
    }

    private fun applyPromoCode() {
        val code = promoCode.text.toString()
        if (code.isBlank() || check.value == PromoCodeUiState.Check.Checking) return
        check.value = PromoCodeUiState.Check.Checking
        viewModelScope.launch {
            when (val result = checkPromoCode(code)) {
                // On success the check stays Checking while the screen closes, so the code can't be applied twice.
                is PromoCodeCheckResult.Valid -> send(PromoCodeEffect.CloseWithResult(result.promoCode))
                PromoCodeCheckResult.NotFound -> {
                    analytics.log(PromoCodeRejectedAnalyticsEvent(code))
                    showCheckError(R.string.promo_not_found)
                }
                is PromoCodeCheckResult.Error -> showCheckError(R.string.promo_check_error)
            }
        }
    }

    private fun showCheckError(@StringRes message: Int) {
        check.value = PromoCodeUiState.Check.Failed(UiText.Resource(message))
    }

    // Any edit of the code, typed or picked from the hint, clears the previous check's error.
    private fun clearCheckErrorOnEdit() {
        viewModelScope.launch {
            snapshotFlow { promoCode.text.toString() }
                .collect {
                    check.update { current ->
                        if (current is PromoCodeUiState.Check.Failed) PromoCodeUiState.Check.Idle else current
                    }
                }
        }
    }

    private fun send(effect: PromoCodeEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val KEY_PROMO_CODE = "promo_code"
    }
}
