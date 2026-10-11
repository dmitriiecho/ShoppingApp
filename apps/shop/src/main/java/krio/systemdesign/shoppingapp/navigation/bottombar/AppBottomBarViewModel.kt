package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelKey
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelScope
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase

@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
class AppBottomBarViewModel(observeCart: ObserveCartUseCase) : ViewModel() {

    val cartItemCount: StateFlow<Int> = observeCart()
        .map { cart -> cart.items.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )
}
