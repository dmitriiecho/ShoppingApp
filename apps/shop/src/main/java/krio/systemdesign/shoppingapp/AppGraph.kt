package krio.systemdesign.shoppingapp

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.InjectedViewModelFactory

// The app's dependency graph. Metro merges into it every AppScope contribution (@ContributesTo, @ContributesBinding,
// @ContributesIntoSet, @ContributesIntoMap) of this module and its direct dependencies.
@DependencyGraph(AppScope::class)
interface AppGraph {
    val viewModelFactory: InjectedViewModelFactory

    // For Coil's image loader (ShoppingApp).
    val httpClient: HttpClient

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AppGraph
    }
}
