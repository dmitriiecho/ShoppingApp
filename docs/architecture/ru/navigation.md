# Навигация

[English version](../en/navigation.md) · [Все разделы](README.md)

Navigation Compose с типизированными маршрутами (`@Serializable`-объекты и классы). Фичи не знают друг о друге: каждая отдаёт приложению свой граф, а приложение соединяет графы.

<br>
<br>

## Как фичи соединены

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

<br>

> [!TIP]
> Поведение навигации во всём приложении — что делает «Назад» на вкладке, как вкладки ложатся друг на друга — меняется в `AppNavGraph` и нижней панели, а не в фичах.

<br>
<br>

## Граф фичи

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

<br>

> [!IMPORTANT]
> **`onClose` значит «выйти из фичи», а что это значит конкретно, решает тот, кто фичу встроил.** У корня вкладки выходить некуда, поэтому приложение передаёт `{}`; та же фича внутри сценария закрыла бы этот сценарий.

`graph(navController, onClose, …)` везде одинаковая, даже там, где `navController` пока не нужен: фича может обрасти экранами, не меняя сигнатуры.

<br>
<br>

## Вкладки

<img src="../images/product-image-transition.gif" align="right" width="220" alt="Картинка товара перелетает из списка в карточку товара">

**У каждой вкладки свой стек.** Переключение вкладки снимает со стека всё, в том числе каталог ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)), с `saveState`/`restoreState`. Поэтому «Назад» с корня любой вкладки выходит из приложения, а не возвращает в каталог.

**Нижняя панель видна только внутри вкладок.** Оформление заказа её закрывает.

**Картинка товара перелетает между экранами** внутри вкладки (shared element), как на анимации справа. Между вкладками она не летает: пока вкладки переключаются, scope общих переходов равен `null`.

<br clear="right">

<br>
<br>

## Карточка товара во вкладке корзины

<img src="../images/product-in-cart-tab.png" align="right" width="220" alt="Товар, открытый из корзины: внизу выбрана вкладка «Корзина»">

Карточка товара принадлежит каталогу, но товар, открытый из корзины, должен остаться во вкладке корзины, как на скриншоте справа.

Каталог для этого предлагает две вещи:

- **`ProductDetailsRoute`** — интерфейс с аргументами экрана: `productId`, `productName`, `imageUrl`;
- **`productDetailsScreen<T>()`** — добавляет экран под любой маршрут, который этот интерфейс реализует.

Приложение объявляет свой маршрут и добавляет экран во вкладку корзины. ViewModel читает аргументы по именам свойств интерфейса, поэтому работает с любым маршрутом.

<br clear="right">

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute

catalog.productDetailsScreen<CartProductRoute>(onBack = { navController.popBackStack() })
```

<br>
<br>

## Результат экрана

Экран промокода возвращает корзине применённый код:

1. **Корзина открывает экран промокода** и передаёт `resultKey`, как request code: `onOpenPromo(resultKey)`. Так один экран промокода может обслуживать нескольких вызывающих.
2. **Экран промокода закрывается с результатом**, а приложение кладёт код в `savedStateHandle` записи стека корзины под этим ключом, в виде JSON.
3. **Навигация корзины читает результат**, передаёт его во ViewModel событием `OnPromoCodeApplied(promoCode)` и удаляет.

<br>

> [!WARNING]
> Положить результат прямо в `SavedStateHandle` ViewModel'и нельзя: у записи стека и у ViewModel'и это разные объекты, и ViewModel его не увидит.

<br>
<br>

## Навигация идёт через ViewModel

Каждая кнопка, которая куда-то ведёт, включая «Назад» и «Закрыть», отправляет событие. ViewModel отвечает эффектом, а экран переходит внутри `navigate { }` (см. [Экраны](screens.md#эффекты)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// во ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// на экране:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

<br>

<details>
<summary>Как остановлен быстрый двойной тап</summary>

- **`navigate { }` выполняется, только пока экран сверху** (`RESUMED`). После первого перехода экран уже не сверху, поэтому второй переход ждёт и отбрасывается, когда экран останавливается.
- **Уходящий экран не принимает нажатия** ([`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt)). Пока идёт анимация перехода, старый экран ещё виден и раньше ловил второй тап: двойной тап по «Назад» закрывал два экрана.

</details>

<br>
<br>

## Deep links

| Ссылка | Что открывает |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | вкладку каталога |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | вкладку корзины |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | товар поверх каталога |

Ссылка открывается так, как туда пришёл бы пользователь ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

1. **Найти вкладку, которая открывает ссылку.** Если такой нет, ссылка игнорируется.
2. **Переключиться на эту вкладку**, как при нажатии, и вернуться к её корню.
3. **Открыть экран поверх корня.** Поэтому «Назад» с товара из ссылки ведёт в каталог.

Ещё о ссылках:

- **App Links проверены**: debug-ключ лежит в репозитории, а его отпечаток есть в `assetlinks.json` на домене, поэтому ссылки открывает сборка с любого компьютера.
- **Домен и пути записаны дважды**, в intent-filter манифеста и в `DeepLinkConfig`; комментарий в каждом месте указывает на другое.
- **Тестовые страницы** со всеми ссылками, с завершающим слешем и краевыми случаями (несуществующий товар, неизвестный путь) лежат в [`docs/deeplinks/`](../../deeplinks/) и опубликованы на GitHub Pages; их открывает экран настроек.

<br>

<details>
<summary>Почему ссылка запуска открывается только один раз</summary>

`MainActivity` открывает ссылку, с которой приложение запустили, а затем стирает её из intent. Иначе `NavHost` открыл бы её ещё раз сам, с другим стеком. Ссылка не открывается повторно ни после пересоздания экрана (сохранённые экраны и так её показывают), ни при запуске из списка недавних: оттуда Android перезапускает приложение со старым intent.

</details>
