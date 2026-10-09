# Analytics

[Русская версия](../ru/analytics.md) · [All pages](README.md)

The app sends events to two analytics systems. Both are fakes that write to logcat, but the code is shaped as it would be with real SDKs.

&nbsp;

## The parts

The core of analytics lives in `:shared:analytics`, and the clients of the actual systems live in the app:

| Part | Where | What it does |
|---|---|---|
| `AnalyticsEvent` | `:shared:analytics` | a name, typed params and the systems the event goes to |
| `Analytics` | `:shared:analytics` | passes an event to the clients of its systems |
| `AnalyticsSystem` | `:shared:analytics` | the app's systems: `Insights` (product analytics), `AdTracker` (ads) |
| `AnalyticsSystemClient` | `:shared:analytics` | code that sends to one system |
| `FakeInsightsClient`, `FakeAdTrackerClient` | `:apps:shop` | one client per system; they stand in for SDKs such as Firebase Analytics and AppsFlyer |

A ViewModel calls `analytics.log(event)`, and `Analytics` hands the event only to the clients of the systems the event names:

```kotlin
fun log(event: AnalyticsEvent) {
    for (client in clients.filter { it.system in event.systems }) {
        try {
            client.log(event)
        } catch (e: Exception) {
            onClientError(client.system, e)  // a failing system doesn't stop the others
        }
    }
}
```

&nbsp;

## Events

Each event is a class that names its own systems and params:

```kotlin
internal class AddToCartAnalyticsEvent(
    productId: String,
    priceCents: Long,
    screen: AnalyticsScreen,
) : AnalyticsEvent("add_to_cart") {
    override val systems = setOf(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

    override val params = analyticsParams {
        param("product_id", productId)
        param("price_cents", priceCents)
        param("screen", screen.value)
    }
}
```

- **The event decides where it goes.** In practice events are added often and systems change rarely, and a tracking plan routes each event. A routing table in the app was tried and dropped.
- **Params are typed**: text, number or flag, only types every system accepts. No `Map<String, Any>`.
- **Names are explicit strings**, so renaming code doesn't change what the systems receive.
- **An event lives in the feature that sends it** (`impl/…/analytics/`), or in `:shared:analytics` (`event/<topic>/`) when two or more features send it.

&nbsp;

## When events are sent

Events are sent only from ViewModels and only after the action succeeds: "added to cart" is logged once Room has saved the item, not on the tap.

```kotlin
launchCartAction(
    action = { addToCart(product) },
    onSuccess = { analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.CatalogList)) },
)
```

Screen views come from the app, not from screens ([`ScreenViews.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/ScreenViews.kt)). It watches the back stack and reports the screen on top after each navigation and each return to the foreground. Each feature names its own screens, and a screen without a name isn't reported:

```kotlin
fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<CatalogRoutes.ProductList>() -> AnalyticsScreen.CatalogList
    hasRoute<CatalogRoutes.ProductDetails>() -> AnalyticsScreen.ProductDetails
    else -> null
}
```

&nbsp;

## Clients

Each system has exactly one client, and `Analytics` checks it when it is created. Otherwise a system without a client would lose events silently, and a system with two would get them twice:

```kotlin
init {
    val systems = clients.map { it.system }
    require(systems.sorted() == AnalyticsSystem.entries) {
        "Each analytics system needs exactly one client, got $systems"
    }
}
```

&nbsp;

## What may be sent

Free text the user typed isn't sent. Search sends only the query's length: a search field can hold anything, a name or a phone number.

```kotlin
// Only the query's length: the text itself may hold anything the user typed.
internal class ProductsSearchedAnalyticsEvent(queryLength: Int) : AnalyticsEvent("products_searched")
```

Rejected promo codes are the exception: the code is sent as typed, because the field is meant for short codes, and which unknown codes people try (old campaigns, typos) is what that event is for.

&nbsp;

## What was left out on purpose

A few parts that look obvious were left out on purpose:

- **No `:core:analytics` module.** The list of systems belongs to this app, so events and `Analytics` live in `:shared:analytics`; what would stay in `core` is too small for a module.
- **No `Analytics` interface.** There is one implementation; a test gives it `TestAnalyticsClient`s, which record what is sent.
- **No event roles or labels** (product, marketing, ads): an event names its systems directly.
