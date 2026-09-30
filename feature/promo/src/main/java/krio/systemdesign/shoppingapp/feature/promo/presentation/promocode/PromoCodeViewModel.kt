package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.feature.promo.domain.usecase.CheckPromoCodeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoCodeViewModel @Inject constructor(
    private val checkPromoCode: CheckPromoCodeUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Введённый код переживает смерть процесса, а загрузка и ошибка — нет:
    // запрос обрывается вместе с процессом.
    private val promoCode = savedStateHandle.getStateFlow(KEY_PROMO_CODE, "")
    private val requestState = MutableStateFlow(RequestState())

    val uiState: StateFlow<PromoCodeUiState> = combine(
        promoCode,
        requestState,
    ) { code, request ->
        PromoCodeUiState(
            promoCode = code,
            isLoading = request.isLoading,
            error = request.error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PromoCodeUiState(promoCode = promoCode.value),
    )

    private val _effects = Channel<PromoCodeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: PromoCodeEvent) {
        when (event) {
            is PromoCodeEvent.OnPromoCodeChange -> {
                savedStateHandle[KEY_PROMO_CODE] = event.value
                requestState.update { it.copy(error = null) }
            }
            PromoCodeEvent.OnApplyClick -> applyPromoCode()
        }
    }

    private fun applyPromoCode() {
        val code = promoCode.value
        if (code.isBlank() || requestState.value.isLoading) return
        requestState.value = RequestState(isLoading = true)
        viewModelScope.launch {
            // При успехе isLoading остаётся true, пока экран закрывается, чтобы код не применили дважды.
            checkPromoCode(code)
                .onSuccess { promoCode -> send(PromoCodeEffect.CloseWithResult(promoCode)) }
                .onFailure { error ->
                    requestState.value = RequestState(
                        error = error.message ?: "Не удалось проверить промокод",
                    )
                }
        }
    }

    private fun send(effect: PromoCodeEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private data class RequestState(
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    private companion object {
        const val KEY_PROMO_CODE = "promo_code"
    }
}
