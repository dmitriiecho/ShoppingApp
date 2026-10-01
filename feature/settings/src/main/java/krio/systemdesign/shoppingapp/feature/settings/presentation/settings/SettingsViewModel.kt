package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnDeepLinksPageClick -> send(SettingsEffect.OpenUrl(DeepLinkConfig.TEST_PAGE_URI))
            SettingsEvent.OnBackClick -> send(SettingsEffect.NavigateBack)
        }
    }

    private fun send(effect: SettingsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
