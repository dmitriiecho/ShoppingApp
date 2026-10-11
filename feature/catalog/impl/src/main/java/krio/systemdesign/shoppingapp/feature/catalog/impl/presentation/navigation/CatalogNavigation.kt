package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import krio.systemdesign.shoppingapp.core.composeutils.animation.sharedElementScreen
import krio.systemdesign.shoppingapp.core.composeutils.navigation.Navigator
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.ProductDetailsScreen
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.ProductListScreen

class CatalogNavigationScope internal constructor(internal val scope: EntryProviderScope<NavKey>)

val EntryProviderScope<NavKey>.catalog: CatalogNavigationScope
    get() = CatalogNavigationScope(this)

// onClose exits the whole catalog feature. The catalog can be the root of a tab, where there is nothing to close,
// or it can be embedded in a flow the user returns back from.
fun CatalogNavigationScope.entries(
    navigator: Navigator,
    onClose: () -> Unit,
) {
    // Product images fly between the list and the product details screen.
    scope.entry<CatalogRoutes.ProductList>(metadata = sharedElementScreen()) {
        ProductListScreen(
            onBack = onClose,
            onOpenProduct = { productId, productName, imageUrl ->
                navigator.navigate(CatalogRoutes.ProductDetails(productId, productName, imageUrl))
            },
        )
    }
    productDetailsEntry(CatalogRoutes.ProductDetails::class, serializer(), onBack = navigator::goBack)
}

// Product details screen inside another feature (for example, the cart), under that feature's own key.
inline fun <reified T> CatalogNavigationScope.productDetailsEntry(
    noinline onBack: () -> Unit,
) where T : NavKey, T : ProductDetailsRoute {
    productDetailsEntry(T::class, serializer<T>(), onBack)
}

@PublishedApi
internal fun <T> CatalogNavigationScope.productDetailsEntry(
    keyClass: KClass<T>,
    keySerializer: KSerializer<T>,
    onBack: () -> Unit,
) where T : NavKey, T : ProductDetailsRoute {
    scope.addEntryProvider(keyClass, metadata = sharedElementScreen()) { key ->
        ProductDetailsScreen(
            onBack = onBack,
            viewModel = injectedViewModel(arguments = key, serializer = keySerializer),
        )
    }
}

// The catalog's links: they open the catalog tab, with the product on top of the list.
val catalogDeepLinks: List<UriDeepLinkMatcher<NavKey>> = listOf(
    UriDeepLinkMatcher(DeepLinkUri(CATALOG_DEEP_LINK), CatalogRoutes.ProductList.serializer()),
    UriDeepLinkMatcher(DeepLinkUri(PRODUCT_DEEP_LINK), CatalogRoutes.ProductDetails.serializer()),
)

private const val CATALOG_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/catalog"

// Explicit rather than built from ProductDetails fields: the link format is public and must not change
// with the key. Only productId comes from a link: productName and imageUrl would be shown and loaded as is.
private const val PRODUCT_DEEP_LINK = "${DeepLinkConfig.BASE_URL}/product/{productId}"
