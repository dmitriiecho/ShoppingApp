package krio.systemdesign.shoppingapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import krio.systemdesign.shoppingapp.analytics.ScreenViewedAnalyticsEvent
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelKey
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelScope
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveThemeModeUseCase

@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
class MainViewModel(
    observeThemeMode: ObserveThemeModeUseCase,
    private val analytics: Analytics,
) : ViewModel() {

    private val _deepLinks = Channel<Uri>(Channel.BUFFERED)
    val deepLinks: Flow<Uri> = _deepLinks.receiveAsFlow()

    val themeMode: StateFlow<ThemeMode?> = observeThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    fun openDeepLink(uri: Uri) {
        _deepLinks.trySend(uri)
    }

    fun onScreenView(screen: AnalyticsScreen) {
        analytics.log(ScreenViewedAnalyticsEvent(screen))
    }
}
