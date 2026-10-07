# Навигация

[English version](../en/navigation.md) · [Все разделы](README.md)

Navigation Compose с типизированными маршрутами (`@Serializable`-объекты и классы). Фичи не знают друг о друге: каждая отдаёт приложению свой граф, а приложение соединяет графы.

### Как фичи соединены

Приложение собирает три вкладки и оформление заказа поверх них. Все переходы между фичами — это колбэки, которые [`AppNavGraph.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavGraph.kt) передаёт в графы фич:

| Откуда | Куда | Колбэк |
|---|---|---|
| Корзина | Карточка товара (во вкладке корзины) | `onOpenProduct` |
| Корзина | Экран промокода | `onOpenPromo(resultKey)` |
| Экран промокода | обратно в корзину, с применённым кодом | `onCloseWithResult(resultKey, promoCode)` |
| Корзина | Оформление заказа (поверх вкладок) | `onOpenCheckout` |

Корзина не знает ни экрана промокода, ни оформления, она просто вызывает колбэк:

```kotlin
cart.cartGraph(
    navController = navController,
    onClose = {}, // Tab root: nothing to close.
    onOpenCheckout = { navController.navigate(CheckoutRoutes.Graph) },
    onOpenPromo = { resultKey -> navController.navigate(PromoRoutes.Graph(resultKey = resultKey)) },
    // ...
)
```

> [!TIP]
> Поведение навигации во всём приложении — что делает «Назад» на вкладке, как вкладки ложатся друг на друга — меняется в `AppNavGraph` и нижней панели, а не в фичах.

### Граф фичи

У всех фич одинаковое устройство, даже у фичи с одним экраном:

```kotlin
object CartRoutes {
    @Serializable data object Graph            // публичный: по нему приложение открывает фичу
    @Serializable internal data object Cart    // собственные экраны фичи
}

fun CartNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,                       // выйти из фичи целиком
    onOpenCheckout: () -> Unit,                // выход в другую фичу
    // ...
)
```

> [!IMPORTANT]
> **`onClose` значит «выйти из фичи», а что это значит конкретно, решает тот, кто фичу встроил.** У корня вкладки выходить некуда, поэтому приложение передаёт `{}`; та же фича внутри сценария закрыла бы этот сценарий.

`graph(navController, onClose, …)` везде одинаковая, даже там, где `navController` пока не нужен: фича может обрасти экранами, не меняя сигнатуры.

### Вкладки

У каждой вкладки свой стек. Переход на вкладку снимает со стека всё, в том числе каталог, и сохраняет стек ушедшей вкладки ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)):

```kotlin
navigate(route) {
    popUpTo(graph.id) { saveState = true }  // снять всё, но запомнить стек вкладки
    launchSingleTop = true
    restoreState = true                     // вернуть стек вкладки, на которую перешли
}
```

Поэтому «Назад» с корня любой вкладки выходит из приложения, а не возвращает в каталог.

Нижняя панель видна только внутри вкладок. Вне их, на оформлении заказа, её просто нет:

```kotlin
val currentTab = navBackStackEntry?.destination?.bottomTab() ?: return
```

Картинка товара перелетает между экранами внутри вкладки (shared element), но не между вкладками: пока вкладки переключаются, scope общих переходов не передаётся.

```kotlin
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavHost(/* ... */)
}
```

### Карточка товара во вкладке корзины

Карточка товара принадлежит каталогу, но товар, открытый из корзины, должен остаться во вкладке корзины. Каталог для этого даёт интерфейс с аргументами экрана и функцию, которая добавляет экран под любой маршрут с этим интерфейсом. Приложение объявляет свой маршрут:

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute

catalog.productDetailsScreen<CartProductRoute>(onBack = { navController.popBackStack() })
```

ViewModel не знает, каким маршрутом её открыли, и читает аргументы по именам свойств интерфейса:

```kotlin
private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
```

### Результат экрана

Экран промокода возвращает корзине применённый код. Корзина открывает его с `resultKey`, как с request code, а приложение, закрывая экран промокода, кладёт результат в `savedStateHandle` записи корзины под этим ключом:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.set(resultKey, Json.encodeToString(promoCode))
},
```

Навигация корзины читает результат, передаёт его во ViewModel событием и удаляет:

```kotlin
LaunchedEffect(promoResult) {
    promoResult?.let { result ->
        viewModel.onEvent(CartEvent.OnPromoCodeApplied(Json.decodeFromString<PromoCode>(result)))
        entry.savedStateHandle.remove<String>(CartResults.PROMO_RESULT_KEY)
    }
}
```

> [!WARNING]
> Положить результат прямо в `SavedStateHandle` ViewModel'и нельзя: у записи стека и у ViewModel'и это разные объекты, и ViewModel его не увидит.

### Навигация идёт через ViewModel

Каждая кнопка, которая куда-то ведёт, включая «Назад» и «Закрыть», отправляет событие. ViewModel отвечает эффектом, а экран переходит внутри `navigate { }` (см. [Экраны](screens.md#эффекты)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// во ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// на экране:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

`navigate { }` выполняется, только пока экран сверху. После первого перехода экран уже не сверху, так что второй переход от быстрого двойного тапа ждёт и отбрасывается:

```kotlin
fun navigate(block: () -> Unit) {
    scope.launch { lifecycle.withResumed(block) }
}
```

<details>
<summary>Вторая защита: уходящий экран не принимает нажатия</summary>

Пока идёт анимация перехода, старый экран ещё виден и раньше ловил второй тап: двойной тап по «Назад» закрывал два экрана. [`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt) не пропускает нажатия, пока верхний экран не стал `RESUMED`.

</details>

### Deep links

| Ссылка | Что открывает |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | вкладку каталога |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | вкладку корзины |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | товар поверх каталога |

Ссылка открывается так, как туда пришёл бы пользователь ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

```kotlin
// Вкладка, которая открывает ссылку; если такой нет, ссылка игнорируется
val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(link) } ?: return
val tabRoot = tabGraph(tab).findStartDestination()

navigateToBottomTab(tab)                       // как нажатие на вкладку
popBackStack(tabRoot.id, inclusive = false)    // к корню вкладки
if (!tabRoot.hasDeepLink(link)) navigate(link) // экран поверх корня
```

Поэтому «Назад» с товара из ссылки ведёт в каталог.

- **App Links проверены**: debug-ключ лежит в репозитории, а его отпечаток есть в `assetlinks.json` на домене, поэтому ссылки открывает сборка с любого компьютера.
- **Домен и пути записаны дважды**, в intent-filter манифеста и в `DeepLinkConfig`; комментарий в каждом месте указывает на другое.
- **Тестовые страницы** со всеми ссылками, с завершающим слешем и краевыми случаями (несуществующий товар, неизвестный путь) лежат в [`docs/deeplinks/`](../../deeplinks/) и опубликованы на GitHub Pages; их открывает экран настроек.

<details>
<summary>Почему ссылка запуска открывается только один раз</summary>

`MainActivity` открывает ссылку, с которой приложение запустили, только при первом запуске и не из списка недавних (оттуда Android перезапускает приложение со старым intent), а затем стирает её, чтобы `NavHost` не открыл её ещё раз сам:

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
intent.data = null
```

</details>
