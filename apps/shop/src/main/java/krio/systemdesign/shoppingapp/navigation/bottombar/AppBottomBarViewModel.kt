package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase

@HiltViewModel
class AppBottomBarViewModel @Inject constructor(observeCart: ObserveCartUseCase) : ViewModel() {

    val cartItemCount: StateFlow<Int> = observeCart()
        .map { cart -> cart.items.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )
}
