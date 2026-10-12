package krio.systemdesign.shoppingapp

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

// The app's dependency graph on Android. Metro merges into it every AppScope contribution (@ContributesTo,
// @ContributesBinding, @ContributesIntoSet, @ContributesIntoMap) of this module and its direct dependencies.
@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AndroidAppGraph
    }
}
