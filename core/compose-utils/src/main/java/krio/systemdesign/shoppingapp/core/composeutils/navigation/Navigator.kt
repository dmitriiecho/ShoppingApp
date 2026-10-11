package krio.systemdesign.shoppingapp.core.composeutils.navigation

import androidx.navigation3.runtime.NavKey

// How a feature moves between its own screens. The app implements it and decides which back stack a screen goes to
// (a tab's or the one over the tabs).
interface Navigator {
    fun navigate(key: NavKey)

    fun goBack()
}
