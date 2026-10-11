package krio.systemdesign.shoppingapp.core.composeutils.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.MapKey
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

// The scope of ViewModelGraph.
abstract class ViewModelScope private constructor()

// Puts a ViewModel into ViewModelGraph under its own class: @ViewModelKey @ContributesIntoMap(ViewModelScope::class).
@MapKey(implicitClassKey = true)
annotation class ViewModelKey(val value: KClass<out ViewModel> = Nothing::class)

// A graph made for each new ViewModel, like Hilt's ViewModelComponent: the app's bindings plus the ViewModel's own
// SavedStateHandle, which a ViewModel takes in its constructor.
@GraphExtension(ViewModelScope::class)
interface ViewModelGraph {
    val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun create(@Provides savedStateHandle: SavedStateHandle): ViewModelGraph
    }
}
