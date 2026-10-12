package krio.systemdesign.shoppingapp.core.composeutils.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

// The app's InjectedViewModelFactory, provided at the root of the UI (MainActivity).
val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("No LocalViewModelFactory: provide it at the root of the UI")
}

// The ViewModel of the current owner (a screen's back stack entry), with its dependencies injected.
@Composable
inline fun <reified VM : ViewModel> injectedViewModel(): VM = viewModel(factory = LocalViewModelFactory.current)
