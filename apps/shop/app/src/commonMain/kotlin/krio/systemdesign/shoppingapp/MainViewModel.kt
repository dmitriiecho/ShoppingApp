package krio.systemdesign.shoppingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import krio.systemdesign.shoppingapp.analytics.ScreenViewedAnalyticsEvent
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelKey
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelScope
import krio.systemdesign.shoppingapp.crossfeature.analytics.Analytics
import krio.systemdesign.shoppingapp.crossfeature.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.crossfeature.domain.usecase.ObserveThemeModeUseCase

@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
class MainViewModel(
    observeThemeMode: ObserveThemeModeUseCase,
    private val analytics: Analytics,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode?> = observeThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    fun onScreenView(screen: AnalyticsScreen) {
        analytics.log(ScreenViewedAnalyticsEvent(screen))
    }
}
