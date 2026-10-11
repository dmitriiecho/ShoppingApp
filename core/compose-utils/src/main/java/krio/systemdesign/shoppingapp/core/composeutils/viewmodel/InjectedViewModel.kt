package krio.systemdesign.shoppingapp.core.composeutils.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.savedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlinx.serialization.KSerializer

// The app's InjectedViewModelFactory, provided at the root of the UI (MainActivity).
val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("No LocalViewModelFactory: provide it at the root of the UI")
}

// The ViewModel of the current owner (a screen's back stack entry), with its dependencies injected.
@Composable
inline fun <reified VM : ViewModel> injectedViewModel(): VM = viewModel(factory = LocalViewModelFactory.current)

// The same, with the fields of a navigation key put into the ViewModel's SavedStateHandle, as Navigation 2 did with
// route arguments: the ViewModel reads them by the key's property names. Taken from the key each time the ViewModel
// is created, so they come back after process death with the saved back stack.
@Composable
inline fun <reified VM : ViewModel, A : Any> injectedViewModel(
    arguments: A,
    serializer: KSerializer<A>,
): VM {
    val owner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner" }
    val ownerExtras = (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
        ?: CreationExtras.Empty
    val extras = MutableCreationExtras(ownerExtras).apply {
        this[DEFAULT_ARGS_KEY] = savedState {
            ownerExtras[DEFAULT_ARGS_KEY]?.let(::putAll)
            putAll(encodeToSavedState(serializer, arguments))
        }
    }
    return viewModel(viewModelStoreOwner = owner, factory = LocalViewModelFactory.current, extras = extras)
}
