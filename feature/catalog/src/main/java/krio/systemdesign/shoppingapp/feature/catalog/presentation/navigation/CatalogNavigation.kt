package krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails.ProductDetailsScreen
import krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist.ProductListScreen

class CatalogNavigationScope(val builder: NavGraphBuilder)

val NavGraphBuilder.catalog: CatalogNavigationScope
    get() = CatalogNavigationScope(this)

// onClose — выход из фичи каталога целиком. Каталог может быть корнем вкладки, где закрывать некуда,
// а может быть встроен во флоу, из которого пользователь возвращается назад.
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
                    basePath = "${DeepLinkConfig.BASE_URI}/catalog",
                ),
            ),
        ) {
            ProductListScreen(
                onBack = onClose,
                onOpenProduct = { productId, productName ->
                    navController.navigate(
                        CatalogRoutes.ProductDetails(
                            productId = productId,
                            productName = productName,
                        ),
                    )
                }
            )
        }

        composable<CatalogRoutes.ProductDetails>(
            deepLinks = listOf(
                // Шаблон задан явно, а не собран из полей ProductDetails: формат ссылки публичный и не должен
                // меняться вместе с маршрутом, а productName из ссылки подставлялся бы в заголовок как есть.
                navDeepLink {
                    uriPattern = "${DeepLinkConfig.BASE_URI}/product/{productId}"
                },
            ),
        ) {
            ProductDetailsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}

inline fun <reified T> CatalogNavigationScope.productDetailsScreen(
    noinline onBack: () -> Unit,
) where T : Any, T : ProductDetailsRoute {
    builder.composable<T> {
        ProductDetailsScreen(
            onBack = onBack,
        )
    }
}
