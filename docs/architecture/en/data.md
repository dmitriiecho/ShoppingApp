# Data

[Русская версия](../ru/data.md) · [All pages](README.md)

## Layers

| Where | What |
|---|---|
| `:shared:domain` | models, repository interfaces and use cases that two or more features use; pure Kotlin, no Android |
| `:shared:data` | their implementations: Room, DataStore, the cart API; all `internal` |
| `feature/<name>/impl/…/domain`, `…/data` | a feature's own models, repositories and use cases (the catalog's products, promo code checks) |

A ViewModel talks to use cases; a use case talks to a repository; a repository talks to Room, DataStore or the server.

## Use cases

- **A use case is one action through a repository**: `AddToCartUseCase`, `ValidateCartUseCase`, `PlaceOrderUseCase`. Many are one line long; they are kept for every action, so a ViewModel never depends on a repository.
- **Rules live in models, not in use cases**: the cart's totals in `Cart`, what the last check found and what is still unfixed in `CartValidation`, the stock limit in `canAddOneMore`.
- **A name that promises more than the code does gets a comment**: `PlaceOrderUseCase` only empties the cart, because the server has no order endpoint.

## Repositories

- **A repository interface speaks only domain models.** Room entities and server DTOs stay inside `data` and are mapped with `toDomain()` / `toEntity()`.
- **A repository with one source works with it directly**: `ProductRepositoryImpl` calls `ProductApi`, `AppSettingsRepositoryImpl` calls DataStore.
- **The cart has two sources**, and each is a class of its own: [`LocalCartDataSource`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/source/LocalCartDataSource.kt) (Room: transactions, merging a product that is already in the cart) and [`CartValidatorDataSource`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/source/CartValidatorDataSource.kt) (the server check). The repository joins them and sees only domain models, and the long Room logic stays apart from the network code. They have no interfaces: each has one implementation.

## Results and errors

Every failure has an explicit type; no exception type is read as something it may not be (an `IOException` is not "no internet": files throw it too).

| Operation | Returns |
|---|---|
| succeeds or fails, nothing else | `kotlin.Result<T>`: `CartRepository.addItem`, `getProducts` |
| has outcomes that aren't errors | a sealed type of its own, in the module that owns the operation: `ProductLoadResult` (`Success` / `NotFound` / `Error`), `PromoCodeCheckResult`, `CartValidationResult` |

Two wrappers turn exceptions into these results:

- **[`networkCall`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkResult.kt)** returns `NetworkResult`: `Success`, `HttpError` (the server answered with an error code) or `Failure` (no answer, or one that can't be read). 4xx is logged as a warning, since 404 for an unknown promo code is a normal answer; 5xx and broken responses as errors.
- **[`databaseCall`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/database/DatabaseCall.kt)** catches only `SQLiteException` (a full disk, a corrupted file). A failed `require` is a bug: it crashes instead of passing for a database error.

Both let coroutine cancellation through.

## Local storage

| Storage | Holds |
|---|---|
| Room (`shopping.db`) | cart items and the applied promo code; the schema is exported to `shared/data/schemas/` |
| DataStore Preferences | the theme and the request delay from the settings |

The app isn't released, so schema changes need no migrations yet: on a schema change a debug build recreates the database.

## Network

- **One `OkHttpClient`** for Retrofit and for Coil's image loading: shared connections and timeouts.
- **Interceptors come from Hilt as a set** (`@ApplicationInterceptor`), so `:shared:data` adds the request delay without `:core:network` knowing about it. The HTTP log is added only in debug.
- **The request delay** from the settings waits before every server request, images included, to show how screens behave while waiting.
- **Plain HTTP is allowed only for the server's address** (`network_security_config.xml`): it has no domain, so no HTTPS.

## Prices

- **Every price is in US dollars, as a `Long` number of cents**: `14999` is $149.99. Server JSON, DTOs, Room and domain models use the same numbers.
- **Shown only through [`formatPrice`](../../../shared/ui/src/main/java/krio/systemdesign/shoppingapp/shared/ui/text/PriceFormat.kt)**: "$1,234.56" whatever the phone's language.
- **No `Money` value class.** It could live only in the domain: `:shared:ui` takes plain values, and a domain type in UI state would be unstable for Compose. Most code with prices is UI, so the type would protect little; it is worth adding together with a second currency or taxes.

## The contract with the server

The server's API and data rules are in [`server/README.md`](../../../server/README.md). What the app relies on:

- **DTOs are copies of the server's**, with a comment pointing to the other side.
- **Paging by page number is safe**: the server never removes products and adds new ones at the end. A renamed product can still move within search results, so `ProductPagingSource` drops repeated ids.
- **The server decides whether checkout opens.** The cart shows what its last check found, but "Checkout" opens only on a fresh `Success` from the server.
- **A promo code's percent never changes**, so the server only checks that the code still exists.
- **The settings' test items rely on four products** keeping their stock and prices; a server test checks them.
