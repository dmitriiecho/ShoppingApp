# Data

[Русская версия](../ru/data.md) · [All pages](README.md)

A ViewModel talks to use cases, a use case to a repository, a repository to Room, DataStore or the server. Which of these are shared and which belong to one feature follows the rule from [Modules](modules.md#where-new-code-goes).

&nbsp;

## Layers

Shared models and their storage live in `shared/`, and what one feature needs stays in that feature:

| Where | What |
|---|---|
| `:shared:domain` | models, repository interfaces and use cases that two or more features use; pure Kotlin, no Android |
| `:shared:data` | their implementations: Room, DataStore, the cart API; all `internal` |
| `feature/<name>/impl/…/domain`, `…/data` | a feature's own models, repositories and use cases: the catalog's products, promo code checks |

&nbsp;

## Use cases

A use case is one action through a repository. Many are one line long, but there is one for every action, so a ViewModel never depends on a repository:

```kotlin
class AddToCartUseCase @Inject constructor(private val cartRepository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Result<Unit> =
        cartRepository.addItem(product, quantity)
}
```

Rules live in models, not in use cases. For example, the cart computes its own totals:

```kotlin
data class Cart(val items: List<CartItem>, val promoCode: PromoCode? = null) {
    fun subtotal(): Long = items.sumOf { it.price * it.quantity }
    fun discount(): Long = promoCode?.discountFor(subtotal()) ?: 0
    fun totalPrice(): Long = subtotal() - discount()
}
```

In the same way `CartValidation` knows what the last cart check found and what is still unfixed, and `canAddOneMore` knows the stock limit.

A name that promises more than the code does gets a comment:

```kotlin
// Orders aren't sent anywhere: the server has no endpoint for them.
// Placing one only resets the cart (items and promo code).
suspend operator fun invoke(): Result<Unit> = cartRepository.reset()
```

&nbsp;

## Repositories

A repository interface speaks only domain models. Room entities and server DTOs stay inside `data` and are mapped with `toDomain()` / `toEntity()`.

A repository with one source works with it directly: `ProductRepositoryImpl` calls `ProductApi`, `AppSettingsRepositoryImpl` calls DataStore.

The cart has two sources, and each is a class of its own:

```kotlin
internal class CartRepositoryImpl @Inject constructor(
    private val localCart: LocalCartDataSource,      // Room: transactions, merging a product already in the cart
    private val validator: CartValidatorDataSource,  // the server check
) : CartRepository
```

The repository joins them and sees only domain models, and the long Room logic stays apart from the network code. The sources have no interfaces: each has one implementation.

&nbsp;

## Results and errors

Every failure has an explicit type. An operation that only succeeds or fails returns `kotlin.Result`. An operation with outcomes that aren't errors has a sealed type of its own:

```kotlin
sealed interface ProductLoadResult {
    data class Success(val product: Product) : ProductLoadResult
    data object NotFound : ProductLoadResult   // a link to a product that doesn't exist: retrying won't help
    data class Error(val error: Throwable) : ProductLoadResult
}
```

`PromoCodeCheckResult` and `CartValidationResult` are built the same way.

Two wrappers turn exceptions into these results. [`networkCall`](../../../core/network/src/main/java/krio/systemdesign/shoppingapp/core/network/NetworkResult.kt) returns a `NetworkResult`, and the repository turns it into its operation's result:

```kotlin
when (val result = networkCall { api.getProduct(productId) }) {
    is NetworkResult.Success -> ProductLoadResult.Success(result.body.toDomain())
    is NetworkResult.HttpError ->
        if (result.code == HTTP_NOT_FOUND) ProductLoadResult.NotFound else ProductLoadResult.Error(result.error)
    is NetworkResult.Failure -> ProductLoadResult.Error(result.error)
}
```

[`databaseCall`](../../../shared/data/src/main/java/krio/systemdesign/shoppingapp/shared/data/database/DatabaseCall.kt) catches only SQLite errors. A failed `require` is a bug, and the app crashes instead of passing it off as a database error:

```kotlin
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Timber.e(e, "Database operation failed")
    Result.failure(e)
}
```

> [!NOTE]
> A broad exception type is never read as something it may not be: an `IOException` is not "no internet", files throw it too.

&nbsp;

## Local storage

The cart and the settings are stored on the device:

| Storage | Holds |
|---|---|
| Room (`shopping.db`) | cart items and the applied promo code; the schema is exported to `shared/data/schemas/` |
| DataStore Preferences | the theme and the request delay from the settings |

The app isn't released, so schema changes need no migrations yet: on a schema change a debug build recreates the database.

&nbsp;

## Network

Retrofit and Coil's image loading share one `OkHttpClient`: the same connections and timeouts. Interceptors come into it from Hilt as a set, so `:shared:data` adds the request delay without `:core:network` knowing about it:

```kotlin
@Binds
@IntoSet
@ApplicationInterceptor
abstract fun bindNetworkDelayInterceptor(impl: NetworkDelayInterceptor): Interceptor
```

- **The request delay** from the settings waits before every server request, images included, to show how screens behave while waiting.
- **The HTTP log** is added only in debug.
- **Plain HTTP is allowed only for the server's address** (`network_security_config.xml`): it has no domain, so no HTTPS.

&nbsp;

## Prices

Every price is in US dollars, as a `Long` number of cents: `14999` is $149.99. Server JSON, DTOs, Room and domain models use the same numbers. Prices are shown only through [`formatPrice`](../../../shared/ui/src/main/java/krio/systemdesign/shoppingapp/shared/ui/text/PriceFormat.kt), in US format whatever the phone's language:

```kotlin
fun formatPrice(amountCents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(BigDecimal.valueOf(amountCents, 2))
```

There is no `Money` value class. It could live only in the domain: `:shared:ui` takes plain values, and a domain type in UI state would be unstable for Compose. Most code with prices is UI, so the type would protect little. It is worth adding together with a second currency or taxes.

&nbsp;

## The contract with the server

The server's API and data rules are in [`server/README.md`](../../../server/README.md). What the app relies on:

- **DTOs are copies of the server's**, with a comment pointing to the other side. The JSON samples in [`server/api-samples/`](../../../server/api-samples/) keep them in step: [both sides' tests](testing.md#contract-with-the-server) check them.
- **Paging by page number is safe**: the server never removes products and adds new ones at the end. A renamed product can still move within search results, so `ProductPagingSource` drops repeated ids.
- **The server decides whether checkout opens.** The cart shows what its last check found, but "Checkout" opens only on a fresh `Success` from the server.
- **A promo code's percent never changes**, so the server only checks that the code still exists.
- **The settings' test items rely on four products** keeping their stock and prices; a server test checks them.
