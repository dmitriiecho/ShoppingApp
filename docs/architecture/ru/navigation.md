# Навигация

[English version](../en/navigation.md) · [Все разделы](README.md)

Навигация построена на Navigation Compose с типизированными маршрутами (`@Serializable`-объекты и классы). Фичи не знают друг о друге: каждая отдаёт приложению свой граф, а приложение связывает графы между собой.

&nbsp;

## Как связаны фичи

Приложение собирает три вкладки и экран оформления заказа поверх них. Любой переход из одной фичи в другую — это колбэк, который [`AppNavGraph.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavGraph.kt) передаёт в графы фич:

| Откуда | Куда | Колбэк |
|---|---|---|
| Корзина | Карточка товара (во вкладке корзины) | `onOpenProduct` |
| Корзина | Экран промокода | `onOpenPromo(resultKey)` |
| Экран промокода | обратно в корзину, с применённым кодом | `onCloseWithResult(resultKey, promoCode)` |
| Корзина | Оформление заказа (поверх вкладок) | `onOpenCheckout` |

Корзина ничего не знает ни об экране промокода, ни об оформлении заказа — она просто вызывает колбэк:

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
> Поведение навигации во всём приложении (что делает «Назад» на вкладке, как вкладки хранят свои стеки) меняется в `AppNavGraph` и нижней панели, а не в фичах.

&nbsp;

## Граф фичи

Все фичи устроены одинаково, даже те, где всего один экран:

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
> **`onClose` означает «выйти из фичи», а что именно это значит, решает тот, кто встроил фичу.** С корня вкладки выходить некуда, поэтому приложение передаёт `{}`; внутри сценария та же фича закрыла бы весь сценарий.

Сигнатура `graph(navController, onClose, …)` везде одинаковая, даже там, где `navController` пока не нужен: так в фичу можно добавлять экраны, не меняя сигнатуру.

&nbsp;

## Вкладки

У каждой вкладки свой стек. При переходе на вкладку со стека снимается всё, в том числе каталог, а стек вкладки, с которой ушли, сохраняется ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)):

```kotlin
navigate(route) {
    popUpTo(graph.id) { saveState = true }  // снять всё, но запомнить стек вкладки
    launchSingleTop = true
    restoreState = true                     // вернуть стек вкладки, на которую перешли
}
```

Поэтому «Назад» с корня любой вкладки закрывает приложение, а не возвращает в каталог.

Нижняя панель видна только внутри вкладок. Оформление заказа находится вне их, поэтому там панели нет:

```kotlin
val currentTab = navBackStackEntry?.destination?.bottomTab() ?: return
```

Картинка товара перелетает между экранами внутри вкладки (shared element), но не между вкладками: пока вкладки переключаются, scope общих переходов дальше не передаётся.

```kotlin
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavHost(/* ... */)
}
```

&nbsp;

## Карточка товара во вкладке корзины

Карточка товара принадлежит каталогу, но товар, открытый из корзины, должен остаться во вкладке корзины. Для этого каталог предоставляет интерфейс с аргументами экрана и функцию, которая добавляет экран под любой маршрут, реализующий этот интерфейс. Приложение объявляет собственный маршрут:

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

&nbsp;

## Результат экрана

Экран промокода возвращает корзине применённый код. Корзина открывает его с `resultKey` — по аналогии с request code, — а приложение при закрытии экрана промокода кладёт результат под этим ключом в `savedStateHandle` записи корзины в стеке:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.set(resultKey, Json.encodeToString(promoCode))
},
```

Навигационный код корзины читает результат, передаёт его во ViewModel в виде события и удаляет:

```kotlin
LaunchedEffect(promoResult) {
    promoResult?.let { result ->
        viewModel.onEvent(CartEvent.OnPromoCodeApplied(Json.decodeFromString<PromoCode>(result)))
        entry.savedStateHandle.remove<String>(CartResults.PROMO_RESULT_KEY)
    }
}
```

> [!WARNING]
> Положить результат сразу в `SavedStateHandle` ViewModel нельзя: у записи стека и у ViewModel это разные объекты, и ViewModel результат не увидит.

&nbsp;

## Навигация идёт через ViewModel

Каждая кнопка, которая куда-то ведёт, включая «Назад» и «Закрыть», отправляет событие. ViewModel отвечает эффектом, а экран выполняет переход внутри `navigate { }` (см. [Экраны](screens.md#эффекты)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// во ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// на экране:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

`navigate { }` выполняется, только пока экран находится сверху. После первого перехода экран уже не сверху, поэтому второй переход от быстрого двойного нажатия ждёт, а затем отбрасывается:

```kotlin
fun navigate(block: () -> Unit) {
    scope.launch { lifecycle.withResumed(block) }
}
```

<details>
<summary>Вторая защита: уходящий экран не реагирует на нажатия</summary>

Пока идёт анимация перехода, старый экран ещё виден, и раньше он ловил второе нажатие: двойное нажатие на «Назад» закрывало два экрана. [`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt) игнорирует нажатия, пока верхний экран не перейдёт в `RESUMED`.

</details>

&nbsp;

## Deep links

Приложение обрабатывает три вида ссылок:

| Ссылка | Что открывает |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | вкладку каталога |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | вкладку корзины |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | товар поверх каталога |

Ссылка открывает экран так же, как до него дошёл бы пользователь ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

```kotlin
// Вкладка, которая обрабатывает ссылку; если такой нет, ссылка игнорируется
val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(link) } ?: return
val tabRoot = tabGraph(tab).findStartDestination()

navigateToBottomTab(tab)                       // как нажатие на вкладку
popBackStack(tabRoot.id, inclusive = false)    // к корню вкладки
if (!tabRoot.hasDeepLink(link)) navigate(link) // экран поверх корня
```

Поэтому «Назад» с товара, открытого по ссылке, ведёт в каталог.

- **App Links подтверждены**: debug-ключ хранится в репозитории, а его отпечаток указан в `assetlinks.json` на домене, поэтому ссылки открывает сборка с любого компьютера.
- **Домен и пути записаны в двух местах**: в intent-filter манифеста и в `DeepLinkConfig`; комментарий в каждом месте указывает на другое.
- **Тестовые страницы** со всеми ссылками, включая варианты с завершающим слешем и краевые случаи (несуществующий товар, неизвестный путь), лежат в [`docs/deeplinks/`](../../deeplinks/) и опубликованы на GitHub Pages; открыть их можно с экрана настроек.

<details>
<summary>Почему ссылка запуска открывается только один раз</summary>

`MainActivity` открывает ссылку, с которой запустили приложение, только при первом запуске, а не при возврате из списка недавних (тогда Android перезапускает приложение со старым intent). Затем ссылка стирается, чтобы `NavHost` не открыл её повторно сам:

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
intent.data = null
```

</details>
