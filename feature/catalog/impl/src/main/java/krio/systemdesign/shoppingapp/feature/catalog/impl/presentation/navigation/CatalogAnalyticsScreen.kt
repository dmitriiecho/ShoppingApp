package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavKey.catalogAnalyticsScreen(): AnalyticsScreen? = when (this) {
    CatalogRoutes.ProductList -> AnalyticsScreen.CatalogList
    is CatalogRoutes.ProductDetails -> AnalyticsScreen.ProductDetails
    else -> null
}
