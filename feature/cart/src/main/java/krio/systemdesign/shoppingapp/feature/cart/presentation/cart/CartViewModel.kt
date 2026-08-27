package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CartUiState(
    val appliedPromoCode: String? = null,
    val showPromoSuccess: Boolean = false,
)

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun onPromoApplied(promoCode: String) {
        _uiState.update {
            it.copy(
                appliedPromoCode = promoCode,
                showPromoSuccess = true,
            )
        }
    }
}
