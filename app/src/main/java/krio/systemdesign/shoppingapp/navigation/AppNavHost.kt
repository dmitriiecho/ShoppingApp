package krio.systemdesign.shoppingapp.navigation

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.cart
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.graph
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.graph as cartGraph
import krio.systemdesign.shoppingapp.feature.cart.presentation.navigation.toCartPromoResult
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.catalog
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.graph as catalogGraph
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.productDetailsScreen
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.CheckoutRoutes
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.checkout
import krio.systemdesign.shoppingapp.feature.checkout.presentation.navigation.graph as checkoutGraph
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.PromoRoutes
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.graph as promoGraph
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.promo
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.SettingsRoutes
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.graph as settingsGraph
import krio.systemdesign.shoppingapp.feature.settings.presentation.navigation.settings
import krio.systemdesign.shoppingapp.navigation.bottombar.AppBottomBar
import krio.systemdesign.shoppingapp.navigation.bottombar.BottomNavRoutes
import krio.systemdesign.shoppingapp.navigation.bottombar.navigateToBottomTab
import kotlinx.coroutines.flow.Flow

@Composable
fun AppNavHost(
    deepLinks: Flow<Uri>,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { AppBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavRoutes.CatalogTab,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                // Пока экраны сменяют друг друга, уходящий экран ещё виден и принимает нажатия:
                // двойной тап по «Назад» закрывал два экрана, а по товару открывал две карточки.
                // Поэтому касания доходят до экранов, только когда верхний экран полностью открыт.
                .blockTouchesDuringTransitions(navController),
        ) {
            navigation<BottomNavRoutes.CatalogTab>(
                startDestination = CatalogRoutes.Graph,
            ) {
                catalog.catalogGraph(
                    navController = navController,
                    onClose = {
                        // Каталог — корень вкладки, закрывать его некуда.
                    },
                )
            }

            navigation<BottomNavRoutes.CartTab>(
                startDestination = CartRoutes.Graph,
            ) {
                cart.cartGraph(
                    navController = navController,
                    onClose = {
                        // Корзина — корень вкладки, закрывать её некуда.
                    },
                    onOpenCheckout = {
                        navController.navigate(CheckoutRoutes.Graph)
                    },
                    onOpenPromo = { resultKey ->
                        navController.navigate(PromoRoutes.Graph(resultKey = resultKey))
                    },
                    onOpenProduct = { productId, productName ->
                        navController.navigate(
                            CustomProductRoute(
                                productId = productId,
                                productName = productName,
                            ),
                        )
                    },
                )

                promo.promoGraph(
                    navController = navController,
                    onClose = {
                        navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
                    },
                    onCloseWithResult = { resultKey, promoCode ->
                        navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(resultKey, promoCode.toCartPromoResult())
                    },
                )

                catalog.productDetailsScreen<CustomProductRoute>(
                    onBack = { navController.popBackStack() },
                )
            }

            navigation<BottomNavRoutes.SettingsTab>(
                startDestination = SettingsRoutes.Graph,
            ) {
                settings.settingsGraph(
                    navController = navController,
                    onClose = {
                        // Настройки — корень вкладки, закрывать их некуда.
                    },
                )
            }

            checkout.checkoutGraph(
                navController = navController,
                onClose = {
                    navController.popBackStack<CheckoutRoutes.Graph>(inclusive = true)
                },
            )
        }

        // Граф задаётся внутри NavHost, поэтому ссылки открываем только после него.
        LaunchedEffect(navController, deepLinks) {
            deepLinks.collect { navController.openDeepLink(it) }
        }
    }
}

// Открываем ссылку так же, как пользователь открыл бы экран сам.
// Поэтому с /cart «Назад» выходит из приложения, а с /product/{id} возвращает в каталог.
private fun NavHostController.openDeepLink(uri: Uri) {
    // Шаблоны ссылок записаны без слеша в конце, а /cart/ и /product/1/ должны открываться так же.
    val path = uri.encodedPath?.trimEnd('/')
    val link = NavDeepLinkRequest.Builder.fromUri(uri.buildUpon().encodedPath(path).build()).build()

    // Ищем вкладку, в которой есть экран для этой ссылки. Если такой нет (например, /catalog/shoes), пропускаем.
    val tab = listOf(BottomNavRoutes.CatalogTab, BottomNavRoutes.CartTab)
        .firstOrNull { tabGraph(it).hasDeepLink(link) }
        ?: return
    // Первый экран вкладки: список товаров или корзина.
    val tabRoot = tabGraph(tab).findStartDestination()

    // Переключаемся на вкладку, как при нажатии в нижней панели.
    navigateToBottomTab(tab)
    // Закрываем экраны, открытые во вкладке поверх первого.
    popBackStack(tabRoot.id, inclusive = false)
    // Если ссылка ведёт не на первый экран вкладки (например, на товар), открываем нужный экран поверх.
    if (!tabRoot.hasDeepLink(link)) {
        navigate(link)
    }
}

private fun NavHostController.tabGraph(tab: Any): NavGraph = graph.findNode(tab) as NavGraph

// Верхний экран становится RESUMED, только когда анимация перехода к нему закончилась.
// Проверяем в момент касания, а не при перерисовке: второе нажатие может прийти раньше неё.
private fun Modifier.blockTouchesDuringTransitions(navController: NavHostController): Modifier =
    pointerInput(navController) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val topScreenState = navController.currentBackStackEntry?.lifecycle?.currentState
                if (topScreenState != Lifecycle.State.RESUMED) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
