package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.SettingsScreen

class SettingsNavigationScope internal constructor(internal val scope: EntryProviderScope<NavKey>)

val EntryProviderScope<NavKey>.settings: SettingsNavigationScope
    get() = SettingsNavigationScope(this)

// onClose exits the whole settings feature. Settings can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun SettingsNavigationScope.entries(
    navigator: Navigator,
    onClose: () -> Unit,
) {
    scope.entry<SettingsRoutes.Settings> {
        SettingsScreen(onBack = onClose)
    }
}
