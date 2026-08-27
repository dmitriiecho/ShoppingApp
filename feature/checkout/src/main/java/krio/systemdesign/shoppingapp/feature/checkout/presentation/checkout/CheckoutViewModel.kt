package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CheckoutUiState(
    val isSubmitting: Boolean = false,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
}
