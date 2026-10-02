package krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.core.ui.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails.ProductDetailsScreen
import krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist.ProductListScreen

class CatalogNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.catalog: CatalogNavigationScope
    get() = CatalogNavigationScope(this)

// onClose exits the whole catalog feature. The catalog can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun CatalogNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
) {
    builder.navigation<CatalogRoutes.Graph>(
        startDestination = CatalogRoutes.ProductList,
    ) {
        composable<CatalogRoutes.ProductList>(
            deepLinks = listOf(
                navDeepLink<CatalogRoutes.ProductList>(
                    basePath = "${DeepLinkConfig.BASE_URL}/catalog",
                ),
            ),
        ) {
            // Product images fly between the list and the product details screen.
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                ProductListScreen(
                    onBack = onClose,
                    onOpenProduct = { productId, productName, imageUrl ->
                        navController.navigate(
                            CatalogRoutes.ProductDetails(
                                productId = productId,
                                productName = productName,
                                imageUrl = imageUrl,
                            ),
                        )
                    }
                )
            }
        }

        composable<CatalogRoutes.ProductDetails>(
            deepLinks = listOf(
                // The pattern is explicit rather than built from ProductDetails fields: the link format is public
                // and must not change with the route, and productName from a link would go into the title as is.
                navDeepLink {
                    uriPattern = "${DeepLinkConfig.BASE_URL}/product/{productId}"
                },
            ),
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                ProductDetailsScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

// Product details screen inside another feature (for example, the cart).
inline fun <reified T> CatalogNavigationScope.productDetailsScreen(
    noinline onBack: () -> Unit,
) where T : Any, T : ProductDetailsRoute {
    builder.composable<T> {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            ProductDetailsScreen(
                onBack = onBack,
            )
        }
    }
}
