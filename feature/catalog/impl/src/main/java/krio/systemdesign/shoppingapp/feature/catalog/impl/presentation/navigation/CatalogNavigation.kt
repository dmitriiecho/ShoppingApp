package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import krio.systemdesign.shoppingapp.core.composeutils.animation.LocalNavAnimatedVisibilityScope
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.ProductDetailsScreen
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.ProductListScreen

class CatalogNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.catalog: CatalogNavigationScope
    get() = CatalogNavigationScope(this)

// onClose exits the whole catalog feature. The catalog can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun CatalogNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,
) {
    builder.navigation<CatalogRoutes.Graph>(startDestination = CatalogRoutes.ProductList) {
        productList(
            onBack = onClose,
            onOpenProduct = { productId, productName, imageUrl ->
                navController.navigate(CatalogRoutes.ProductDetails(productId, productName, imageUrl))
            },
        )
        productDetails(
            onBack = { navController.popBackStack() },
        )
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

private fun NavGraphBuilder.productList(
    onBack: () -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
) {
    composable<CatalogRoutes.ProductList>(
        deepLinks = listOf(navDeepLink<CatalogRoutes.ProductList>(basePath = CATALOG_DEEP_LINK)),
    ) {
        // Product images fly between the list and the product details screen.
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            ProductListScreen(
                onBack = onBack,
                onOpenProduct = onOpenProduct,
            )
        }
    }
}

private fun NavGraphBuilder.productDetails(onBack: () -> Unit) {
    composable<CatalogRoutes.ProductDetails>(
        deepLinks = listOf(navDeepLink { uriPattern = PRODUCT_DEEP_LINK }),
    ) {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            ProductDetailsScreen(
                onBack = onBack,
            )
        }
    }
}

private const val CATALOG_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/catalog"

// Explicit rather than built from ProductDetails fields: the link format is public and must not change
// with the route, and a link must not set productName or imageUrl (they'd be shown and loaded as is).
private const val PRODUCT_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/product/{productId}"
