# Экраны

[English version](../en/screens.md) · [Все разделы](README.md)

У экрана одно состояние UI, набор событий от пользователя и набор разовых эффектов. ViewModel превращает события в новое состояние или в эффект; экран только рисует состояние и реагирует на эффекты.

&nbsp;

## Файлы экрана

Все экраны устроены одинаково. На примере корзины (`feature/cart/impl/.../presentation/cart/`):

| Файл | Что в нём |
|---|---|
| `CartScreen.kt` | две перегрузки `CartScreen`, приватный `CartContent` и мелкие помощники вроде верхней панели |
| `CartViewModel.kt` | состояние, обработка событий, эффекты |
| `CartUiState.kt` | состояние и его вложенные типы |
| `CartEvent.kt`, `CartEffect.kt` | что делает пользователь и что происходит один раз |
| `components/` | секции со своими превью: `CartItemCard`, `CartTotalsCard`, … |

&nbsp;

## Две перегрузки экрана

Вместо пары Route + Screen у экрана две функции с одним именем. Первая берёт ViewModel и собирает состояние и эффекты, вторая только рисует:

```kotlin
@Composable
internal fun CartScreen(
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    // ...
    viewModel: CartViewModel = hiltViewModel(),
)

@Composable
internal fun CartScreen(
    uiState: CartUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier,
)
```

Вторая не знает о Hilt, поэтому её можно показать в превью в любом состоянии.

`onEvent` доходит только до этого файла: до экрана, `CartContent` и мелких приватных помощников. Секции из `components/` получают конкретные колбэки, поэтому не знают событий экрана и показываются в превью сами по себе:

```kotlin
CartItemCard(
    item = item,
    onQuantityChange = { productId, quantity -> onEvent(CartEvent.OnQuantityChange(productId, quantity)) },
    onRemove = { onEvent(CartEvent.OnRemoveFromCartClick(it)) },
    // ...
)
```

&nbsp;

## UI state

Состояние — один `StateFlow` на экран: `combine` по источникам и `stateIn`. Типы, из которых состоит только состояние, вложены в него:

```kotlin
data class CartUiState(
    val content: Content = Content.Loading,
    val isOpeningCheckout: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    val canCheckout: Boolean
        get() = content is Content.Loaded && content.allowsCheckout && !isOpeningCheckout

    sealed interface Content {
        data object Loading : Content
        data class Loaded(val items: List<Item>, /* ... */) : Content
    }
}
```

- **Вложенные типы пишутся через владельца**: `CartUiState.Item`, а внутри владельца имя не повторяется: `PromoCodeUiState.Check`, а не `PromoCodeCheck`.
- **Поля сгруппированы по секциям экрана**, а не идут плоским списком: у оформления это `order`, `address`, `paymentMethod`.
- **Загрузка — это состояние, а не флаг и не `null`**: sealed `Loading` / `Loaded` / `Error`, везде с этими именами. `null` значит только «неизвестно», например имя товара, которое не передала deep link.
- **Состояние хранит то, что экран рисует.** Доменную модель, которую экран показывает как есть, состояние берёт как есть: товары заказа в оформлении, промокоды в подсказке. Свою UI-модель на простых значениях экран заводит, когда ему нужны готовые решения или [стабильность Compose](#стабильность-compose): `CartUiState.Item` хранит `canAddOneMore` и проблемы товара, поэтому «+» перерисовывает только свою карточку.
- **Повторяющиеся условия — геттеры** состояния, как `canCheckout` выше.

Проверка перед действием читает сам источник, а не `uiState`: состояние получает изменение только после `combine`, чуть позже. Иначе быстрый двойной тап по «Оформить заказ» оформлял заказ дважды:

```kotlin
private fun submitOrder() {
    if (submission.value is Submission.Submitting || !uiState.value.canSubmit) return
    val order = uiState.value.order as? CheckoutUiState.Order.Loaded ?: return
    submission.value = Submission.Submitting(order)
    // ...
}
```

&nbsp;

## Эффекты

Навигация и снекбары происходят один раз, поэтому это эффекты, которые идут через `Channel`, а не часть состояния: состоянию понадобилось бы ещё одно событие «обработано». Экран обрабатывает их через [`ObserveEffects`](../../../core/compose-utils/src/main/java/krio/systemdesign/shoppingapp/core/composeutils/effects/ObserveEffects.kt):

```kotlin
ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
        CartEffect.NavigateToCheckout -> navigate { onOpenCheckout() }
        is CartEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        // ...
    }
}
```

Внутри `ObserveEffects` эффекты собираются, пока экран виден, и обрабатываются сразу на `Main.immediate`:

```kotlin
lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
    withContext(Dispatchers.Main.immediate) {
        effects.collect { effect -> scope.currentOnEffect(effect) }
    }
}
```

- **Эффект, отправленный в фоне**, ждёт в канале и обрабатывается, когда пользователь вернётся.
- **`Main.immediate`** нужен, чтобы эффект обрабатывался прямо внутри `send` ViewModel'и: если бы между ними была пауза, экран, остановившийся в этот момент, забрал бы эффект и потерял его.
- **`navigate { }` выполняется, только пока экран сверху**, а **`showSnackbar` работает в своей корутине**: снекбар ждёт, пока его закроют, и задержал бы следующий эффект.

На экран приходится один эффект снекбара, `ShowSnackBar(UiText)`: ViewModel собирает текст из строкового ресурса, не имея доступа к ресурсам.

&nbsp;

## Поля ввода

Текст поля — это `TextFieldState`, который создаёт ViewModel и кладёт в состояние одним и тем же объектом на весь экран. `savedTextField` сохраняет его в `SavedStateHandle`, так что текст переживает смерть процесса:

```kotlin
private val searchQuery: TextFieldState = savedStateHandle.savedTextField(KEY_SEARCH_QUERY)
```

- **Нет события `OnTextChange`.** Поле меняет состояние на месте, поэтому ввод никогда не ждёт flow и не теряет символы.
- **Значения, выведенные из текста, — геттеры** состояния. Compose отслеживает это чтение:

  ```kotlin
  val canApply: Boolean
      get() = promoCode.text.isNotBlank() && !isChecking
  ```

- **`snapshotFlow { state.text }` во ViewModel — для действий**: поиск, сброс ошибки после правки.

&nbsp;

## Сохранение состояния

То, что пользователь ввёл или открыл, переживает смерть процесса; то, что делал запрос, — нет:

| Переживает | Как |
|---|---|
| текст в полях | `savedStateHandle.savedTextField(key)` |
| открытый диалог, выбранный вариант | `savedStateHandle.getStateFlow(key, default)` |
| страница каталога, на которой был пользователь | номер страницы в `SavedStateHandle`; список открывается на ней |
| результаты запросов | не хранятся: экран загружает их заново |

&nbsp;

## Стабильность Compose

Strong skipping включён, поэтому по умолчанию ничего не размечается: composable пропускается, если получает тот же объект. Чинится только то, что показали отчёты компилятора и лог перерисовок.

Чтобы объект менялся, только когда меняется его источник, источник преобразуется до `combine`:

```kotlin
val uiState = combine(
    observeCart().map { it.toOrder() },  // новый заказ — только при изменении корзины
    paymentMethod,
    submission,
) { cartOrder, payment, latestSubmission -> CheckoutUiState(/* ... */) }
```

Модели из `:shared:domain` для Compose нестабильны: этот модуль собирается без компилятора Compose, и аннотаций Compose там нет. Это важно для списка, карточки которого меняются по одной, как в корзине: там экран получает свою UI-модель на простых значениях, `CartUiState.Item`. Экран, который показывает данные как есть, как оформление, оставляет доменные модели.

Колбэки списка принимают id элемента, а не захватывают сам элемент. Так все карточки получают одни и те же лямбды, и «+» перерисовывает только свою карточку:

```kotlin
onQuantityChange: (productId: String, quantity: Int) -> Unit
```

&nbsp;

## Широкие экраны

Телефоны показывают приложение только вертикально, и это решение. Android 16+ всё равно поворачивает его на экранах от 600 dp: планшетах, раскрытых раскладушках, окнах на десктопе. Поэтому экран раскладывается под ширину:

- **Содержимое остаётся колонкой** шириной `ContentMaxWidth` (600 dp) посередине, через `Modifier.contentWidth()` из `:core:designsystem`. Модификатор ставится внутри прокручиваемого контейнера, поэтому экран листается и за краями колонки.
- **Шапка выровнена по колонке** через `Modifier.contentBarWidth()`: ширина колонки плюс отступ экрана с каждой стороны, поэтому заголовок и кнопки стоят над краями содержимого, как на телефоне.
- **Карточка товара в горизонтальном положении** ставит картинку и описание рядом: под квадратной картинкой описание начиналось бы за краем экрана.

&nbsp;

## Компоненты и превью

Экран собирается из готовых компонентов: каждый стилизованный элемент берётся из `:core:designsystem`, `:shared:ui` или модуля `ui` фичи (см. [Модули](modules.md#куда-класть-новый-код)). Сам экран компоненты Material не стилизует.

Превью лежат рядом с тем, что показывают, приватные, в светлой и тёмной теме. Файл экрана показывает состояния, которые выглядят по-разному как экран целиком; состояния секции показываются в файле секции. Каждое превью — ещё и [скриншот-тест](testing.md#скриншоты), поэтому состояние с превью защищено от изменений, которых никто не просил.
