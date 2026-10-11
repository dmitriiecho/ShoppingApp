package krio.systemdesign.shoppingapp.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlin.test.Test
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation.CheckoutRoutes
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation.PromoRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomTab

class AppNavigatorTest {

    private val product = CartProductRoute(productId = "1")
    private val promo = PromoRoutes.PromoCode(resultKey = "result")

    @Test
    fun `screen opens on top of the current tab`() {
        val state = testNavigationState(currentTab = BottomTab.Cart)

        AppNavigator(state).navigate(product)

        assertThat(state.tabStacks.getValue(BottomTab.Cart)).containsExactly(CartRoutes.Cart, product)
    }

    @Test
    fun `screen opens over the tabs while something is open there`() {
        val state = testNavigationState(overTabs = listOf(CheckoutRoutes.Checkout))

        AppNavigator(state).navigate(product)

        assertThat(state.overTabs).containsExactly(CheckoutRoutes.Checkout, product)
    }

    @Test
    fun `back closes the screen over the tabs before the tab's own`() {
        val state = testNavigationState(
            currentTab = BottomTab.Cart,
            cartStack = listOf(CartRoutes.Cart, product),
            overTabs = listOf(CheckoutRoutes.Checkout),
        )

        AppNavigator(state).goBack()

        assertThat(state.overTabs).isEmpty()
        assertThat(state.tabStacks.getValue(BottomTab.Cart)).containsExactly(CartRoutes.Cart, product)
    }

    @Test
    fun `back keeps the tab root`() {
        val state = testNavigationState(currentTab = BottomTab.Cart)

        AppNavigator(state).goBack()

        assertThat(state.tabStacks.getValue(BottomTab.Cart)).containsExactly(CartRoutes.Cart)
    }

    @Test
    fun `switching tabs keeps the stack of the tab left`() {
        val state = testNavigationState(currentTab = BottomTab.Cart, cartStack = listOf(CartRoutes.Cart, product))
        val navigator = AppNavigator(state)

        navigator.switchTab(BottomTab.Catalog)
        navigator.switchTab(BottomTab.Cart)

        assertThat(state.currentTabStack).containsExactly(CartRoutes.Cart, product)
    }

    @Test
    fun `switching tabs closes the screens over the tabs`() {
        val state = testNavigationState(overTabs = listOf(CheckoutRoutes.Checkout))

        AppNavigator(state).switchTab(BottomTab.Settings)

        assertThat(state.overTabs).isEmpty()
        assertThat(state.currentTab).isEqualTo(BottomTab.Settings)
    }

    @Test
    fun `leaving a feature closes its first screen and everything above it`() {
        val state = testNavigationState(
            currentTab = BottomTab.Cart,
            cartStack = listOf(CartRoutes.Cart, promo, product),
        )

        AppNavigator(state).popUpTo(PromoRoutes.PromoCode::class)

        assertThat(state.currentTabStack).containsExactly(CartRoutes.Cart)
    }

    @Test
    fun `deep link opens its screen over the root of its tab`() {
        val state = testNavigationState(
            currentTab = BottomTab.Cart,
            catalogStack = listOf(CatalogRoutes.ProductList, product),
            overTabs = listOf(CheckoutRoutes.Checkout),
        )

        AppNavigator(state).openDeepLink(BottomTab.Catalog, product)

        assertThat(state.currentTab).isEqualTo(BottomTab.Catalog)
        assertThat(state.currentTabStack).containsExactly(CatalogRoutes.ProductList, product)
        assertThat(state.overTabs).isEmpty()
    }

    @Test
    fun `deep link to a tab root shows just the root`() {
        val state = testNavigationState(cartStack = listOf(CartRoutes.Cart, product))

        AppNavigator(state).openDeepLink(BottomTab.Cart, CartRoutes.Cart)

        assertThat(state.currentTabStack).containsExactly(CartRoutes.Cart)
    }

    private fun testNavigationState(
        currentTab: BottomTab = BottomTab.Catalog,
        catalogStack: List<NavKey> = listOf(BottomTab.Catalog.root),
        cartStack: List<NavKey> = listOf(BottomTab.Cart.root),
        overTabs: List<NavKey> = emptyList(),
    ) = NavigationState(
        currentTab = mutableStateOf(currentTab),
        tabStacks = mapOf(
            BottomTab.Catalog to NavBackStack(*catalogStack.toTypedArray()),
            BottomTab.Cart to NavBackStack(*cartStack.toTypedArray()),
            BottomTab.Settings to NavBackStack(BottomTab.Settings.root),
        ),
        overTabs = NavBackStack(*overTabs.toTypedArray()),
    )
}
