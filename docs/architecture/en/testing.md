# Testing

[Русская версия](../ru/testing.md) · [All pages](README.md)

Tests run on the JVM, with no emulator: the app's logic, its data layer and the server. The app and the server follow the same rules, so a test reads the same on both sides.

&nbsp;

## Running the tests

Each build runs its own tests:

```sh
./gradlew testDebugUnitTest                         # Android modules
./gradlew :shared:domain:test :shared:analytics:test  # pure Kotlin modules
cd server && ./gradlew test                         # the server
```

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

The one exception to the first row: the server's check of its `data/` files is `DataFilesTest`, since those are data, not code.

A helper used by tests of **two or more modules** lives in the **test fixtures** of the module that owns what it replaces; one used by a single module stays in that module's `src/test`. It's the same rule as for [code and components](modules.md#where-new-code-goes):

| Helper | Module | Source set |
|---|---|---|
| `testProduct()`, `testCartItem()`, `testCart()` | `:shared:domain` | `src/testFixtures` |
| `TestAnalyticsClient` | `:shared:analytics` | `src/testFixtures` |
| `networkTest {}` | `:core:network` | `src/testFixtures` |

A module uses another module's fixtures with `testImplementation(testFixtures(project(":shared:domain")))`. Fixtures see only the public API of their module. The [module graph check](modules.md#the-check) looks at `api` and `implementation` only, so test dependencies follow the levels by convention, not by the check.

&nbsp;

## Tools

The libraries work in Kotlin Multiplatform, except those for Android-only code:

| Tool | For |
|---|---|
| `kotlin.test` | `@Test` and the other annotations; tests don't import `org.junit` |
| [AssertK](https://github.com/assertk-org/assertk) | every check: `assertThat(actual).isEqualTo(expected)`, `isInstanceOf<T>()` narrows the type, `assertFailure {}` |
| `kotlinx-coroutines-test` | `runTest` and the virtual clock |
| MockWebServer | a local HTTP server for the network code |

The runner hides behind `kotlin.test`, so tests look the same though the runners differ: JUnit4 in the app (Robolectric runs only on it), JUnit5 on the server.

&nbsp;

## Kinds of tests

Each kind of test starts with one entry function that sets up what the kind needs.

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

### Server

`serverTest {}` starts the server in memory with Ktor's `testApplication`, on the data the test passes (`testShopData(products = …)`), and gives a client that reads JSON with the server's own `serverJson`. More on the server's API in [`server/README.md`](../../../server/README.md).
