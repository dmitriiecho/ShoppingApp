package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<CatalogRoutes.ProductList>() -> AnalyticsScreen.CatalogList
    hasRoute<CatalogRoutes.ProductDetails>() -> AnalyticsScreen.ProductDetails
    else -> null
}
