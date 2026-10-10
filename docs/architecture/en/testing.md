# Testing

[Русская версия](../ru/testing.md) · [All pages](README.md)

Tests run on the JVM, without an emulator. They cover the app's logic, ViewModels, data layer and screens (a screenshot of every preview), its contract with the server, and the server itself. The app and the server follow the same rules, so tests read the same way on both sides.

&nbsp;

## Running the tests

Each build runs its own tests, and CI runs both sets on every pull request:

```sh
./gradlew test                # the app: every module, once, including screenshots
cd server && ./gradlew test   # the server
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # records new screenshots after a UI change
```

Android modules run their unit tests in the debug variant only (`shoppingapp.android.library` turns off the release ones, which would just repeat them), so a single `test` covers Android and pure Kotlin modules alike.

&nbsp;

## How a test is written

Here is a short test from the project:

```kotlin
@Test
fun `discount is the promo code percent of the subtotal`() {
    val cart = testCart(testCartItem(price = 2000), promoCode = PromoCode("SALE10", 10))

    assertThat(cart.discount()).isEqualTo(200)
}
```

- **One behaviour per test.** Several unrelated checks in one test hide each other, because the first failure stops the rest. Cases of the same kind go into a single table with AssertK's `tableOf`, which checks every row and reports all the failing ones. A table can't call suspend functions, so cases that are sent to a server are all requested first and then checked together: `assertThat(statuses.filterValues { it != BadRequest }).isEmpty()` lists every wrong answer.
- **The name states the condition and the expected result**, in backticks: `` `cart with a changed price reports the new price` ``, not `` `price changed` ``.
- **Arrange, act, assert**, separated by blank lines.
- **A test's data lives in the test.** Builders with defaults (`testCartItem(price = 2000)`) let a test set only the values that matter to it. There is no data set shared by all tests, because changing it would break unrelated tests.
- **Working stand-ins instead of mocks.** Repositories are interfaces, so a test uses an in-memory implementation with real behaviour; there is no mocking library.
- **No real time**: coroutine tests run on `runTest` with its virtual clock.
- **Every new test is seen failing at least once**: break the code it checks, watch the test fail, then restore the code. A test that has never failed may not be checking anything.
- **Real data files are checked separately from behaviour**, against a rule ("every image in the catalog has a file") rather than a count.

Comments follow the [code style](build.md#code-style): they appear only where the code can't explain why.

&nbsp;

## Names and places

Everything made for tests starts with `Test` or `test`, as in Now in Android. The `Fake` prefix is already taken: `FakeInsightsClient` and `FakeAdTrackerClient` in `:apps:shop` are production code that stands in for analytics systems.

| What | Name | Example |
|---|---|---|
| Test class | the file it checks + `Test`, in the same package | `Cart.kt` → `CartTest` |
| Stand-in implementation | `Test` + what it replaces | `TestAnalyticsClient` |
| Data builder | `test` + the model | `testCartItem()`, `testCart()` |
| Entry function of a test kind | kind + `Test` | `networkTest {}`, `serverTest {}` |
| Action a test performs | a verb | `typeText()`, `keepCollecting()` |

The only exception to the first row is the server's check of its `data/` files, `DataFilesTest`, since those files are data, not code.

A helper used by the tests of **two or more modules** lives in the **test fixtures** of the module that owns what it replaces; a helper used by a single module stays in that module's `src/test`. It's the same rule as for [code and components](modules.md#where-new-code-goes):

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

A module uses another module's fixtures through `testImplementation(testFixtures(project(":shared:domain")))`. Fixtures see only the public API of their module. The [module graph check](modules.md#the-check) looks only at `api` and `implementation`, so test dependencies follow the levels by convention rather than being enforced.

&nbsp;

## Tools

The libraries all work in Kotlin Multiplatform, except the ones for Android-only code:

| Tool | For |
|---|---|
| `kotlin.test` | `@Test` and the other annotations; tests don't import `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | every check: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` narrows the type, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` and the virtual clock |
| [Turbine](https://github.com/cashapp/turbine) | a screen's one-off effects: `effects.test { awaitItem() }` |
| MockWebServer | a local HTTP server for the network code |
| [Robolectric](https://robolectric.org) | Android classes on the JVM, only for code that can't run without them (Room, screenshots) |
| [Roborazzi](https://github.com/takahirom/roborazzi) | screenshot tests generated from the `@Preview` functions |

The test runner is hidden behind `kotlin.test`, so tests look the same even though the runners differ: JUnit4 in the app (Robolectric works only with it) and JUnit5 on the server.

&nbsp;

## Kinds of tests

Each kind of test starts with an entry function that sets up what that kind of test needs.

### ViewModel

`viewModelTest {}` replaces `Dispatchers.Main`, where `viewModelScope` runs, with a test dispatcher on `runTest`'s virtual clock. The ViewModel gets its real use cases backed by `Test` repositories, so a test sets up the data and the server's answer and checks what the screen receives:

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
- **The state is collected the whole time, just as the screen does it**: `keepCollecting(viewModel.uiState)` right after the ViewModel is created, and then the test reads `viewModel.uiState.value`. A `stateIn(WhileSubscribed)` flow with no collector keeps its initial value. Effects are one-off, so they are checked with Turbine.
- **A Paging list is presented the whole time, just as the screen's list does it**: Paging loads pages only while something presents them, so the catalog's test keeps a `PagingDataPresenter` collecting `viewModel.products` and checks which pages the repository was asked for. `asSnapshot()` doesn't work with a debounce, because it advances the virtual clock until everything is done.
- **Typing goes through `typeText()`**: on a device, Compose applies a change to a field on the next frame, and a test has no frames.
- **Time is virtual**: `advanceTimeBy()` moves it past a debounce or an animation without actually waiting.
- **State saved for process death** is checked only when it is a plain value in `SavedStateHandle` (a dialog, the payment method): the same handle is passed to a new ViewModel. Text fields are saved through an Android `Bundle`, which isn't available in a JVM test.
- **A request in progress** is modelled as a `CompletableDeferred` that the answer waits for: the test changes the cart in the meantime and then completes it.
- **Analytics events don't implement `equals`**, so `TestAnalytics.sentEvents` stores their names and parameters: `assertThat(analytics.sentEvents).containsExactly(CheckoutStartedAnalyticsEvent(itemCount = 3, totalCents = 5000).sent())`.

### Database

`databaseTest {}` provides an empty in-memory `ShoppingDatabase`. Room needs an Android `Context`, so these test classes run on Robolectric; this is the only place where a test imports `org.junit`:

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

Robolectric runs on the app's `targetSdk`: `shoppingapp.android.library` gives the tests of every library module that SDK, its resources and the JVM flags Robolectric needs on Java 17 and later. Without them it would fall back to its oldest SDK, which is older than the app's `minSdk`. DataStore doesn't need a `Context`, so the settings are tested on a real DataStore in a temporary file, without Robolectric.

### Network

`networkTest {}` starts MockWebServer and provides a client for an API interface that reads JSON the same way the app does, using the same [`networkJson`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkJson.kt):

```kotlin
@Test
fun `client error returns HttpError with its code`() = networkTest<TestApi> { server, api ->
    server.enqueue(MockResponse.Builder().code(404).build())

    val result = networkCall { api.item() }

    assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
}
```

### Contract with the server

The app and the server each describe the API's JSON in their own DTOs. What keeps them in sync is [`server/api-samples/`](../../../server/api-samples/): one JSON file for each request or response the app relies on. `apiSample("product.json")` reads a sample on either side, parsed with that side's own `Json`, and the check compares JSON trees, so field order doesn't matter, but a renamed, extra or missing field does. Where each request goes is listed in `requests.json`, as a method and an address: `"product": "GET /products/1"` is the request whose response is `product.json`.

| Side | Checks |
|---|---|
| Server | it accepts the request samples at their addresses and responds exactly like the response samples |
| App | it sends requests exactly like the request samples, to their addresses, and parses the response samples into the expected models |

Changing a format or an address on one side fails that side's tests until the sample, and then the other side, are updated too. The build declares the folder as an input of every test task, so editing a sample makes the tests run again.

### Screenshots

Every `@Preview` is a screenshot test, so nobody has to write them by hand. `shoppingapp.android.screenshots`, which every library module with previews applies, has [Roborazzi](https://github.com/takahirom/roborazzi) render each preview on Robolectric and compare it with the image saved in the module's `screenshots/` folder:

| Command | Does |
|---|---|
| `./gradlew test` | compares every preview with its saved image and fails if they differ |
| `./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true` | renders the previews and saves them as the new images; deletes the images of previews that no longer exist |

These tests don't tell you whether a screen looks right; they catch changes nobody asked for. That's why images are recorded only by a person, deliberately, after a UI change:

1. Change the UI.
2. Check that only what you intended has changed, in one of two ways:
   - **test first**: run `./gradlew test`. Only the previews you meant to change should fail, and each one's `build/outputs/roborazzi/…_compare.png` should highlight in red only what you intended. It reveals a shift of a pixel or two that you'd miss comparing two images side by side;
   - **record first**: record (step 3), then look at which images changed (`git status`, or the images in the IDE). It's quicker, one run instead of two, but the old image is already overwritten, so there is no comparison image.

   Either way, thirty changed images after a change to a single button is a problem worth investigating.
3. Record the images, if you haven't yet: `./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true`, or the Record screenshots run configuration in Android Studio.
4. Commit the images together with the code; the pull request shows the old and new version of each image side by side.

> [!IMPORTANT]
> If you didn't mean to change how the app looks, don't record. In that case a failed screenshot test has caught a real problem: padding that shifted during a refactor, a library update that changed the buttons, a shared component that affected another screen. CI never records images.

- **A changed screen is a failed test** until its new images are recorded and committed.
- **A failed test leaves a comparison image** in the build folder: `build/outputs/roborazzi/*_compare.png`, with the saved image, the difference in red and the new image. In CI these are attached to the run as `changed-screenshots` and shown in a comment on the pull request.
- **The clock is paused**: `PausedClockPreviewTester` renders every preview at its first frame, so an endless animation (the shimmer, a spinner) doesn't hang the test and every run produces the same image.
- **Old images are deleted only when recording**, through that flag: Roborazzi deletes every image the run didn't render, so if the flag were always on, running a single test from Android Studio would delete all the module's other images.
- **A dialog is rendered on the whole screen**, including its dimmed background, so a dialog gets a preview like any other component. Android Studio renders only the main window, so the preview is blank there; Run Preview shows it on a device.
- **Private previews count too**: Slack's lint rules require previews to be private, and the plugin includes them.
- **The UI kit app has no screenshots of its own**: its sections show the same components that are already captured in their own modules.

Google's Compose Preview Screenshot Testing does the same thing with Android Studio's renderer, but it is still in alpha, so the project uses Roborazzi until it becomes stable.

### Server

`serverTest {}` starts the server in memory with Ktor's `testApplication`, using the data and product images the test passes in (`testShopData(products = …)`, `images = mapOf("1.png" to …)`), and provides a client that reads JSON with the server's own `serverJson`. The images live in a temporary folder that is deleted after the test. A request from the samples is sent with `sendApiRequest("product")`, which fails on any status other than success. More on the server's API is in [`server/README.md`](../../../server/README.md).
