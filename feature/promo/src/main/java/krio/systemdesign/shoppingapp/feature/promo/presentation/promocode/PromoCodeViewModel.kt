package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PromoCodeUiState(
    val promoCode: String = "",
)

@HiltViewModel
class PromoCodeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PromoCodeUiState())
    val uiState: StateFlow<PromoCodeUiState> = _uiState.asStateFlow()

    fun onPromoCodeChange(value: String) {
        _uiState.update { it.copy(promoCode = value) }
    }
}
