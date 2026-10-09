# Testing

[Русская версия](../ru/testing.md) · [All pages](README.md)

Tests run on the JVM, with no emulator: the app's logic, its data layer and the server. The app and the server follow the same rules, so a test reads the same on both sides.

&nbsp;

## Running the tests

Each build runs its own tests, and CI runs both on every pull request:

```sh
./gradlew test                # the app: every module, once, screenshots included
cd server && ./gradlew test   # the server
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # saves new screenshots after a UI change
```

Android modules run their unit tests on debug only (`shoppingapp.android.library` turns off release ones, which would repeat them), so one `test` covers Android and pure Kotlin modules alike.

&nbsp;

## How a test is written

A short test of the project:

```kotlin
@Test
fun `discount is the promo code percent of the subtotal`() {
    val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

    assertThat(cart.discount()).isEqualTo(200)
}
```

- **One behaviour per test.** Several unrelated checks in one test hide each other: the first failure stops the rest. Cases of the same kind go into one table with AssertK's `tableOf`, which checks every row and reports all failing ones. A table can't call suspend functions, so cases sent to a server are all asked first and then checked together: `assertThat(statuses.filterValues { it != BadRequest }).isEmpty()` lists every wrong answer.
- **The name says the condition and the expected result**, in backticks: `` `cart with a changed price reports the new price` ``, not `` `price changed` ``.
- **Arrange, act, assert**, separated by blank lines.
- **The test's data is in the test.** Builders with defaults (`testCartItem(price = 2000)`) let a test set only the values that matter to it. There is no shared data set for all tests: changing it would break unrelated tests.
- **Working stand-ins, not mocks.** Repositories are interfaces, so a test uses an in-memory implementation with real behaviour; there is no mocking library.
- **No real time**: coroutine tests run on `runTest` with its virtual clock.
- **A new test is seen failing once**: break the code it checks, watch the test fail, put the code back. A test that never failed may check nothing.
- **Real data files are checked apart from behaviour**, by a rule ("every image in the catalog has a file"), not by a count.

Comments follow the [code style](build.md#code-style): only where the code can't say why.

&nbsp;

## Names and places

Everything made for tests starts with `Test` or `test`, as in Now in Android. The prefix `Fake` is taken: `FakeInsightsClient` and `FakeAdTrackerClient` in `:apps:shop` are real code, stand-ins for analytics systems.

| What | Name | Example |
|---|---|---|
| Test class | the file it checks + `Test`, in the same package | `Cart.kt` → `CartTest` |
| Stand-in implementation | `Test` + what it replaces | `TestAnalyticsClient` |
| Data builder | `test` + the model | `testCartItem()`, `testCart()` |
| Entry function of a test kind | kind + `Test` | `networkTest {}`, `serverTest {}` |
| Action a test does | a verb | `typeText()`, `keepCollecting()` |

The one exception to the first row: the server's check of its `data/` files is `DataFilesTest`, since those are data, not code.

A helper used by tests of **two or more modules** lives in the **test fixtures** of the module that owns what it replaces; one used by a single module stays in that module's `src/test`. It's the same rule as for [code and components](modules.md#where-new-code-goes):

| Helper | Module | Source set |
|---|---|---|
| `testProduct()`, `testCartItem()`, `testCart()` | `:shared:domain` | `src/testFixtures` |
| `TestCartRepository` | `:shared:domain` | `src/testFixtures` |
| `TestAnalyticsClient`, `TestAnalytics` | `:shared:analytics` | `src/testFixtures` |
| `viewModelTest {}`, `keepCollecting()`, `typeText()`, `PausedClockPreviewTester` | `:core:compose-utils` | `src/testFixtures` |
| `networkTest {}`, `apiSample()`, `apiRequest()` | `:core:network` | `src/testFixtures` |
| `databaseTest {}` | `:shared:data` | `src/test` |
| `TestProductRepository` | `:feature:catalog:impl` | `src/test` |
| `TestPromoCodeRepository` | `:feature:promo:impl` | `src/test` |
| `serverTest {}`, `testShopData()`, `testProduct()`, `apiSample()`, `sendApiRequest()` | `server/` | `src/test` |

A module uses another module's fixtures with `testImplementation(testFixtures(project(":shared:domain")))`. Fixtures see only the public API of their module. The [module graph check](modules.md#the-check) looks at `api` and `implementation` only, so test dependencies follow the levels by convention, not by the check.

&nbsp;

## Tools

The libraries work in Kotlin Multiplatform, except those for Android-only code:

| Tool | For |
|---|---|
| `kotlin.test` | `@Test` and the other annotations; tests don't import `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | every check: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` narrows the type, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` and the virtual clock |
| [Turbine](https://github.com/cashapp/turbine) | a screen's one-off effects: `effects.test { awaitItem() }` |
| MockWebServer | a local HTTP server for the network code |
| [Robolectric](https://robolectric.org) | Android classes on the JVM, only for code that can't run without them (Room, screenshots) |
| [Roborazzi](https://github.com/takahirom/roborazzi) | screenshot tests made from the `@Preview` functions |

The runner hides behind `kotlin.test`, so tests look the same though the runners differ: JUnit4 in the app (Robolectric runs only on it), JUnit5 on the server.

&nbsp;

## Kinds of tests

Each kind of test starts with one entry function that sets up what the kind needs.

### ViewModel

`viewModelTest {}` replaces `Dispatchers.Main`, where `viewModelScope` runs, with a test dispatcher on `runTest`'s virtual clock. The ViewModel gets its real use cases on top of `Test` repositories, so a test sets the data and the server's answer and checks what the screen gets:

```kotlin
@Test
fun `checkout of a cart the server changed asks to review the changes`() = viewModelTest {
    cartRepository.validationAnswer = { invalid(ItemIssue.Unavailable("2")) }
    val viewModel = cartViewModel()

    viewModel.effects.test {
        viewModel.onEvent(CartEvent.OnCheckoutClick)

        assertThat(awaitItem()).isEqualTo(snackBar(R.string.cart_changed))
    }
}
```

- **The ViewModel is created inside `viewModelTest {}`**, after `Dispatchers.Main` is replaced.
- **The state is kept collected, as the screen does**: `keepCollecting(viewModel.uiState)` right after creating it, then the test reads `viewModel.uiState.value`. A `stateIn(WhileSubscribed)` flow with no collector keeps its initial value. Effects are one-off, so they are checked with Turbine.
- **A Paging list is kept shown, as the screen's list does**: Paging loads pages only while something presents them, so the catalog's test keeps a `PagingDataPresenter` collecting `viewModel.products` and checks the pages the repository was asked for. `asSnapshot()` doesn't fit a debounce: it moves the virtual clock until everything is done.
- **Typing goes through `typeText()`**: on a device Compose applies a field's change on the next frame, a test has no frames.
- **Time is virtual**: `advanceTimeBy()` moves it past a debounce or an animation without waiting.
- **State saved for process death** is checked only when it is a plain value in `SavedStateHandle` (a dialog, the payment method): the same handle is given to a new ViewModel. Text fields are saved through an Android `Bundle`, which a JVM test doesn't have.
- **A request in progress** is a `CompletableDeferred` the answer waits for: the test changes the cart meanwhile, then completes it.
- **Analytics events have no `equals`**, so `TestAnalytics.sentEvents` holds their name and params: `assertThat(analytics.sentEvents).containsExactly(CartClearedAnalyticsEvent().sent())`.

### Database

`databaseTest {}` gives an empty `ShoppingDatabase` in memory. Room needs an Android `Context`, so these test classes run on Robolectric, the one place a test imports `org.junit`:

```kotlin
@RunWith(RobolectricTestRunner::class)
class LocalCartDataSourceTest {

    @Test
    fun `adding a product already in the cart adds to its quantity`() = databaseTest { database ->
        val cart = cartOf(database)
        cart.addItem(testProduct(id = "1"), quantity = 2)

        cart.addItem(testProduct(id = "1"), quantity = 3)

        assertThat(cart.current().quantityOf("1")).isEqualTo(5)
    }
}
```

Robolectric runs on the app's `targetSdk`: `shoppingapp.android.library` gives every library module's tests that SDK, its resources and the JVM flags Robolectric needs on Java 17+. Without them it would take its oldest SDK, older than the app's `minSdk`. DataStore needs no `Context`, so the settings are tested on a real DataStore in a temporary file, without Robolectric.

### Network

`networkTest {}` starts MockWebServer and gives a client for an API interface that reads JSON as the app does, with the same [`networkJson`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkJson.kt):

```kotlin
@Test
fun `client error returns HttpError with its code`() = networkTest<TestApi> { server, api ->
    server.enqueue(MockResponse.Builder().code(404).build())

    val result = networkCall { api.item() }

    assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
}
```

### Contract with the server

The app and the server each describe the API's JSON in their own DTOs. What keeps them in step is [`server/api-samples/`](../../../server/api-samples/): one JSON file per request or answer the app relies on. `apiSample("product.json")` reads one on both sides, parsed with that side's own `Json`, and the check compares JSON trees, so field order doesn't matter but a renamed, extra or missing field does. Where each request goes is in `requests.json`, as the method and the address: `"product": "GET /products/1"` is the request answered by `product.json`.

| Side | Checks |
|---|---|
| Server | it accepts the request samples at their addresses and answers exactly like the answer samples |
| App | it sends requests exactly like the request samples, to their addresses, and reads the answer samples into the expected models |

A change of a format or an address on one side fails that side's tests, until the sample, and then the other side, are changed too. The build declares the folder as an input of every test task, so an edited sample runs the tests again.

### Screenshots

Every `@Preview` is a screenshot test: nobody writes them. `shoppingapp.android.screenshots`, applied by each module with previews, has [Roborazzi](https://github.com/takahirom/roborazzi) draw each preview on Robolectric and compare it with the image saved in the module's `screenshots/` folder:

| Command | Does |
|---|---|
| `./gradlew test` | compares every preview with its saved image and fails on a difference |
| `./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true` | draws the previews and saves them as the new images; deletes the images of previews that are gone |

These tests don't say whether a screen looks right: they catch changes nobody asked for. So the images are recorded only by a person, on purpose, after a change to the UI:

1. Change the UI.
2. Check that only what you meant changed, in either of two ways:
   - **test first**: `./gradlew test`. Only the previews you meant to change fail, and each one's `build/outputs/roborazzi/…_compare.png` marks in red only what you meant; it shows a shift of a pixel or two that two images side by side hide;
   - **record first**: record (step 3), then look at which images changed (`git status`, or the images in the IDE). Quicker, one run instead of two, but the old image is already overwritten, so there is no comparison image.

   Either way, thirty changed images after a change to one button is the problem to look into.
3. Record, if not done yet: `./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true`.
4. Commit the images with the code; the pull request shows each one's old and new version side by side.

> [!IMPORTANT]
> If you didn't mean to change how the app looks, don't record. A failed screenshot test is then a found problem: a padding that moved in a refactor, a library update that changed the buttons, a shared component that touched another screen. CI never records images.

- **A changed screen is a failed test** until its new images are recorded and committed.
- **A failed one leaves a comparison image** next to the build: `build/outputs/roborazzi/*_compare.png`, the saved image, the difference in red and the new one. In CI they are attached to the run as `changed-screenshots` and shown in a comment on the pull request.
- **The clock is stopped**: `PausedClockPreviewTester` draws every preview at its first frame, so an endless animation (the shimmer, a spinner) doesn't hang the test and every run draws the same image.
- **Old images are deleted only when recording**, by that flag: Roborazzi deletes every image the run didn't draw, so with the flag always on, running one test from Android Studio would delete the rest of the module's images.
- **A dialog is drawn on the whole screen**, its dimmed background included, so a dialog gets a preview like any other component.
- **Private previews count too**: Slack's lint rules make them private, and the plugin includes them.
- **The UI kit app has no screenshots of its own**: its sections show the same components that are already drawn in their own modules.

Google's Compose Preview Screenshot Testing does the same with Android Studio's renderer, but it is still in alpha; Roborazzi it is until that one is stable.

### Server

`serverTest {}` starts the server in memory with Ktor's `testApplication`, on the data the test passes (`testShopData(products = …)`), and gives a client that reads JSON with the server's own `serverJson`. More on the server's API in [`server/README.md`](../../../server/README.md).
