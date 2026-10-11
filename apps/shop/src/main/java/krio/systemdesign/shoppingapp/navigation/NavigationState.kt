package krio.systemdesign.shoppingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.cartNavKeys
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.catalogNavKeys
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.checkoutNavKeys
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.promoNavKeys
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.settingsNavKeys
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab

// What the user has open: the current tab, the back stack of every tab, and the screens over the tabs (checkout).
// Changed only by AppNavigator; NavDisplay shows it.
class NavigationState(
    currentTab: MutableState<BottomTab>,
    val tabStacks: Map<BottomTab, NavBackStack<NavKey>>,
    val overTabs: NavBackStack<NavKey>,
) {
    var currentTab: BottomTab by currentTab

    val currentTabStack: NavBackStack<NavKey>
        get() = tabStacks.getValue(currentTab)

    // The screen on top: over the tabs if something is open there, otherwise the current tab's.
    val currentKey: NavKey
        get() = overTabs.lastOrNull() ?: currentTabStack.last()
}

// Survives configuration changes and process death: the keys are saved with kotlinx.serialization.
@Composable
fun rememberNavigationState(): NavigationState {
    val currentTab = rememberSerializable(serializer = MutableStateSerializer(BottomTab.serializer())) {
        mutableStateOf(BottomTab.Catalog)
    }
    val tabStacks = BottomTab.entries.associateWith { tab -> rememberNavBackStack(navKeysConfiguration, tab.root) }
    val overTabs = rememberNavBackStack(navKeysConfiguration)
    return remember { NavigationState(currentTab, tabStacks, overTabs) }
}

// Every key a back stack can hold, so it can be saved on every platform (rememberNavBackStack without
// a configuration finds them by reflection, on Android only).
private val navKeysConfiguration = SavedStateConfiguration {
    serializersModule = catalogNavKeys +
        cartNavKeys +
        promoNavKeys +
        checkoutNavKeys +
        settingsNavKeys +
        SerializersModule { polymorphic(NavKey::class) { subclass(CartProductRoute::class) } }
}
