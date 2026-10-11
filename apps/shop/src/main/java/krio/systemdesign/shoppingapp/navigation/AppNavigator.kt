package krio.systemdesign.shoppingapp.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab

// Every navigation in the app goes through here and changes NavigationState.
class AppNavigator(private val state: NavigationState) : Navigator {

    // The screens over the tabs while any are open, otherwise the current tab.
    private val activeStack: NavBackStack<NavKey>
        get() = if (state.overTabs.isNotEmpty()) state.overTabs else state.currentTabStack

    override fun navigate(key: NavKey) {
        activeStack.add(key)
    }

    // A tab root stays: Back from it leaves the app (NavDisplay doesn't handle Back with one screen left).
    override fun goBack() {
        if (state.overTabs.isNotEmpty()) {
            state.overTabs.removeAt(state.overTabs.lastIndex)
        } else if (state.currentTabStack.size > 1) {
            state.currentTabStack.removeAt(state.currentTabStack.lastIndex)
        }
    }

    // Each tab keeps its back stack: coming back to a tab shows where the user left it.
    fun switchTab(tab: BottomTab) {
        state.overTabs.clear()
        state.currentTab = tab
    }

    // Screens that cover the tabs and the bottom bar, such as checkout.
    fun openOverTabs(key: NavKey) {
        state.overTabs.add(key)
    }

    fun closeOverTabs() {
        state.overTabs.clear()
    }

    // Leaves a feature: removes its first screen, keyClass, and everything above it.
    fun popUpTo(keyClass: KClass<out NavKey>) {
        val stack = activeStack
        val index = stack.indexOfLast { keyClass.isInstance(it) }
        if (index > 0 || (index == 0 && stack === state.overTabs)) {
            stack.subList(index, stack.size).clear()
        }
    }

    // Opens a screen the way the user would: Back from it goes to its tab's root.
    fun openDeepLink(
        tab: BottomTab,
        key: NavKey,
    ) {
        switchTab(tab)
        val stack = state.currentTabStack
        stack.subList(1, stack.size).clear()
        if (key != tab.root) stack.add(key)
    }
}
