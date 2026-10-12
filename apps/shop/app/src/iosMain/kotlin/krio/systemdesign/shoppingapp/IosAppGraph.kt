package krio.systemdesign.shoppingapp

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

// The app's dependency graph on iOS. Metro merges into it every AppScope contribution (@ContributesTo,
// @ContributesBinding, @ContributesIntoSet, @ContributesIntoMap) of this module, iosMain included, and its direct
// dependencies.
@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph
