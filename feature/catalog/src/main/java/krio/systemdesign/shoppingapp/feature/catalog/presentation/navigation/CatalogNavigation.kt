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
            // Картинки товаров перелетают между списком и карточкой товара.
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
                // Шаблон задан явно, а не собран из полей ProductDetails: формат ссылки публичный и не должен
                // меняться вместе с маршрутом, а productName из ссылки подставлялся бы в заголовок как есть.
                navDeepLink {
                    uriPattern = "${DeepLinkConfig.BASE_URI}/product/{productId}"
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

// Карточка товара внутри другой фичи (например, корзины).
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
