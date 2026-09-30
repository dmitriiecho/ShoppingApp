package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.feature.promo.domain.usecase.CheckPromoCodeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoCodeViewModel @Inject constructor(
    private val checkPromoCode: CheckPromoCodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromoCodeUiState())
    val uiState: StateFlow<PromoCodeUiState> = _uiState.asStateFlow()

    private val _effects = Channel<PromoCodeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: PromoCodeEvent) {
        when (event) {
            is PromoCodeEvent.OnPromoCodeChange -> {
                _uiState.update { it.copy(promoCode = event.value, error = null) }
            }
            PromoCodeEvent.OnApplyClick -> applyPromoCode()
        }
    }

    private fun applyPromoCode() {
        val state = _uiState.value
        if (!state.canApply) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // При успехе isLoading остаётся true, пока экран закрывается, чтобы код не применили дважды.
            checkPromoCode(state.promoCode)
                .onSuccess { promoCode -> send(PromoCodeEffect.CloseWithResult(promoCode)) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Не удалось проверить промокод",
                        )
                    }
                }
        }
    }

    private fun send(effect: PromoCodeEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
