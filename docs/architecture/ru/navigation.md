# Навигация

[English version](../en/navigation.md) · [Все разделы](README.md)

Навигация построена на Navigation 3: экран — это `@Serializable`-ключ, стек — список ключей, которым владеет приложение, а `NavDisplay` показывает его верх. Фичи не знают друг о друге: каждая отдаёт приложению свои экраны, а приложение связывает их между собой.

&nbsp;

## Как связаны фичи

Приложение собирает три вкладки и экран оформления заказа поверх них. Любой переход из одной фичи в другую — это колбэк, который [`AppEntries.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppEntries.kt) передаёт фичам:

| Откуда | Куда | Колбэк |
|---|---|---|
| Корзина | Карточка товара (во вкладке корзины) | `onOpenProduct` |
| Корзина | Экран промокода | `onOpenPromo(resultKey)` |
| Экран промокода | обратно в корзину, с применённым кодом | `onCloseWithResult(resultKey, promoCode)` |
| Корзина | Оформление заказа (поверх вкладок) | `onOpenCheckout` |

Корзина ничего не знает ни об экране промокода, ни об оформлении заказа — она просто вызывает колбэк:

```kotlin
cart.entries(
    navigator = navigator,
    onClose = {}, // Tab root: nothing to close.
    onOpenCheckout = { navigator.openOverTabs(CheckoutRoutes.Checkout) },
    onOpenPromo = { resultKey -> navigator.navigate(PromoRoutes.PromoCode(resultKey)) },
    // ...
)
```

> [!TIP]
> Поведение навигации во всём приложении (что делает «Назад» на вкладке, как вкладки хранят свои стеки) меняется в [`AppNavigator.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt), а не в фичах.

&nbsp;

## Экраны фичи

Все фичи устроены одинаково, даже те, где всего один экран:

```kotlin
object CartRoutes {
    @Serializable data object Cart : NavKey    // публичный: приложение открывает фичу её первым экраном
}

fun CartNavigationScope.entries(
    navigator: Navigator,                      // переходы между экранами самой фичи
    onClose: () -> Unit,                       // выйти из фичи целиком
    onOpenCheckout: () -> Unit,                // выход в другую фичу
    // ...
)

val cartNavKeys: SerializersModule             // ключи фичи, чтобы приложение сохраняло стек
```

> [!IMPORTANT]
> **`onClose` означает «выйти из фичи», а что именно это значит, решает тот, кто встроил фичу.** С корня вкладки выходить некуда, поэтому приложение передаёт `{}`; экран промокода закрывается вместе со всем, что он открыл (`navigator.popUpTo(PromoRoutes.PromoCode::class)`), а оформление заказа — закрытием того, что поверх вкладок.

Сигнатура `entries(navigator, onClose, …)` везде одинаковая, даже там, где `navigator` пока не нужен: так в фичу можно добавлять экраны, не меняя сигнатуру. `Navigator` ([`:core:compose-utils`](../../../core/compose-utils/src/main/java/krio/systemdesign/shoppingapp/core/composeutils/navigation/Navigator.kt)) только открывает экран и возвращается назад; в какой стек попадёт экран, решает приложение.

&nbsp;

## Стеки и вкладки

То, что открыто у пользователя, — это [`NavigationState`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/NavigationState.kt): текущая вкладка, стек каждой вкладки и стек поверх вкладок для оформления заказа. Он переживает смерть процесса: каждая фича регистрирует свои ключи (`cartNavKeys` и подобные), а приложение сохраняет их через kotlinx.serialization, что работало бы на любой платформе. Меняет его только [`AppNavigator`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt):

```kotlin
override fun navigate(key: NavKey) {
    activeStack.add(key)            // поверх вкладок, пока там что-то открыто, иначе в текущую вкладку
}

fun switchTab(tab: BottomTab) {
    state.overTabs.clear()
    state.currentTab = tab          // покинутая вкладка сохраняет свой стек
}
```

`NavDisplay` получает экраны текущей вкладки, а за ними экраны поверх вкладок. У каждого стека своё сохранённое состояние и свои ViewModel, поэтому вкладка, с которой ушли, сохраняет свои экраны как были.

«Назад» с корня вкладки закрывает приложение, а не возвращает в каталог: когда в стеке один экран, `NavDisplay` «Назад» не обрабатывает, и система закрывает приложение.

Нижняя панель видна, только пока поверх вкладок ничего нет, поэтому при оформлении заказа её нет.

Картинка товара перелетает между экранами внутри вкладки (shared element), но не между вкладками. Экраны, где она летает, помечены `sharedElementScreen()`, и приложение отдаёт им анимацию их записи. Пока вкладки переключаются, на экране одновременно экраны двух вкладок, и тогда область перехода shared elements вниз не передаётся:

```kotlin
val isSwitchingTabs = tabsOnScreen.distinct().size > 1
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavDisplay(/* ... */)
}
```

&nbsp;

## Карточка товара во вкладке корзины

Карточка товара принадлежит каталогу, но товар, открытый из корзины, должен остаться во вкладке корзины. Для этого каталог даёт интерфейс с аргументами экрана и функцию, которая добавляет экран под любой ключ, реализующий этот интерфейс. Приложение объявляет свой ключ:

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute, NavKey

catalog.productDetailsEntry<CartProductRoute>(onBack = navigator::goBack)
```

ViewModel не знает, каким ключом её открыли. Поля ключа кладутся в её `SavedStateHandle` (`injectedViewModel(arguments = key, …)`, как Navigation 2 делал с аргументами маршрута), и она читает их по именам свойств интерфейса:

```kotlin
private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
```

&nbsp;

## Результат экрана

Экран промокода возвращает применённый код в корзину через `ResultEventBus` из Navigation 3. Корзина открывает его с `resultKey`, похожим на request code, и когда экран промокода закрывается с кодом, приложение отправляет его под этим ключом:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    resultEventBus.sendResult(resultKey, promoCode)
    navigator.popUpTo(PromoRoutes.PromoCode::class)
},
```

Навигационный код корзины получает результат, передаёт его во ViewModel событием и удаляет, чтобы он не применился ещё раз:

```kotlin
ResultEffect<PromoCode>(CartResults.PROMO_RESULT_KEY) { promoCode ->
    viewModel.onEvent(CartEvent.OnPromoCodeApplied(promoCode))
    resultEventBus.removeResult(CartResults.PROMO_RESULT_KEY)
}
```

> [!NOTE]
> Шина живёт в памяти, поэтому результат, отправленный, но ещё не полученный, пропадёт, если процесс умрёт в этот момент. После получения код сохраняет ViewModel, так что после смерти процесса он в корзине есть.

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

Пока идёт анимация перехода, старый экран ещё виден, и раньше он ловил второе нажатие: двойное нажатие на «Назад» закрывало два экрана. `NavDisplay` держит экран ниже `RESUMED`, пока его переход не закончится, а [`blockTouchesUntilResumed`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/EntryDecorators.kt), декоратор на каждой записи, до этого момента игнорирует нажатия.

</details>

&nbsp;

## Deep links

Приложение обрабатывает три вида ссылок:

| Ссылка | Что открывает |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | вкладку каталога |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | вкладку корзины |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | товар поверх каталога |

Каждая фича объявляет ссылки, которые открывает, матчерами Navigation 3 (`catalogDeepLinks`, `cartDeepLinks`), а приложение знает, в какой вкладке открываются ссылки каждой фичи. Ссылка открывает экран так же, как до него дошёл бы пользователь ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt), [`AppNavigator.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt)):

```kotlin
switchTab(tab)                            // как нажатие на вкладку
stack.subList(1, stack.size).clear()      // к корню вкладки
if (key != tab.root) stack.add(key)       // экран поверх корня
```

Поэтому «Назад» с товара, открытого по ссылке, ведёт в каталог. Ссылка задаёт только `productId`: название и картинка товара приходят с сервера, а не из ссылки.

- **App Links подтверждены**: debug-ключ хранится в репозитории, а его отпечаток указан в `assetlinks.json` на домене, поэтому ссылки открывает сборка с любого компьютера.
- **Домен и пути записаны в двух местах**: в intent-filter манифеста и в `DeepLinkConfig`; комментарий в каждом месте указывает на другое.
- **Тестовые страницы** со всеми ссылками, включая варианты с завершающим слешем и краевые случаи (несуществующий товар, неизвестный путь), лежат в [`docs/deeplinks/`](../../deeplinks/) и опубликованы на GitHub Pages; открыть их можно с экрана настроек.

<details>
<summary>Почему ссылка запуска открывается только один раз</summary>

`MainActivity` открывает ссылку, с которой запустили приложение, только при первом запуске, а не при возврате из списка недавних (тогда Android перезапускает приложение со старым intent):

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
```

</details>
