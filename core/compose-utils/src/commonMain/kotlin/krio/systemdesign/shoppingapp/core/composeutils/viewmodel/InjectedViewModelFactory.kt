package krio.systemdesign.shoppingapp.core.composeutils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass

// Creates every ViewModel of the app. The extras come from the ViewModel's owner (a screen's back stack entry,
// the activity), so the SavedStateHandle holds that owner's saved state and navigation arguments.
@Inject
class InjectedViewModelFactory(private val graphFactory: ViewModelGraph.Factory) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        val graph = graphFactory.create(extras.createSavedStateHandle())
        val provider = checkNotNull(graph.viewModelProviders[modelClass]) {
            "${modelClass.simpleName} is missing @ViewModelKey @ContributesIntoMap(ViewModelScope::class)"
        }
        @Suppress("UNCHECKED_CAST")
        return provider() as T
    }
}
