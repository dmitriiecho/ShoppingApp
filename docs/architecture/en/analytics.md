# Analytics

[Русская версия](../ru/analytics.md) · [All pages](README.md)

The app sends events to two analytics systems. Both are fakes that just write to logcat, but the code is structured the way it would be with real SDKs.

&nbsp;

## How it is put together

The core of analytics lives in `:shared:analytics`, and the clients for the actual systems live in the app:

| Part | Where | What it does |
|---|---|---|
| `AnalyticsEvent` | `:shared:analytics` | a name, typed parameters and the systems the event is sent to |
| `Analytics` | `:shared:analytics` | passes an event to the clients of the event's systems |
| `AnalyticsSystem` | `:shared:analytics` | the app's systems: `Insights` (product analytics), `AdTracker` (ads) |
| `AnalyticsSystemClient` | `:shared:analytics` | code that sends events to one system |
| `FakeInsightsClient`, `FakeAdTrackerClient` | `:apps:shop` | one client per system, standing in for SDKs such as Firebase Analytics and AppsFlyer |

A ViewModel calls `analytics.log(event)`, and `Analytics` passes the event only to the clients of the systems that the event lists:

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

Each event is a class that lists its own systems and parameters:

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

- **The event decides where it goes.** In practice, events are added often while systems rarely change, and the tracking plan specifies where each event goes. A routing table in the app was tried and dropped.
- **Parameters are typed**: text, number or boolean flag, the only types every system accepts. There is no `Map<String, Any>`.
- **Names are explicit strings**, so renaming something in the code doesn't change what the systems receive.
- **An event lives in the feature that sends it** (`impl/…/analytics/`), or in `:shared:analytics` (`event/<topic>/`) when two or more features send it.

&nbsp;

## When events are sent

Events are sent only from ViewModels and only after the action succeeds: "added to cart" is logged once Room has saved the item, not when the button is tapped.

```kotlin
launchCartAction(
    action = { addToCart(product) },
    onSuccess = { analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.CatalogList)) },
)
```

Screen views are reported by the app, not by the screens ([`ScreenViews.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/ScreenViews.kt)). It watches the back stack and reports the screen on top after every navigation and every return to the foreground. Each feature names its own screens, and a screen without a name isn't reported:

```kotlin
fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = when {
    hasRoute<CatalogRoutes.ProductList>() -> AnalyticsScreen.CatalogList
    hasRoute<CatalogRoutes.ProductDetails>() -> AnalyticsScreen.ProductDetails
    else -> null
}
```

&nbsp;

## Clients

Each system has exactly one client, and `Analytics` checks this when it is created. Otherwise a system without a client would silently lose events, and a system with two clients would receive them twice:

```kotlin
init {
    val systems = clients.map { it.system }
    require(systems.sorted() == AnalyticsSystem.entries) {
        "Each analytics system needs exactly one client, got $systems"
    }
}
```

&nbsp;

## What is allowed to be sent

Free text typed by the user is never sent. Search sends only the length of the query, because a search field can contain anything, even a name or a phone number.

```kotlin
// Only the query's length: the text itself may hold anything the user typed.
internal class ProductsSearchedAnalyticsEvent(queryLength: Int) : AnalyticsEvent("products_searched")
```

Rejected promo codes are the exception: the code is sent exactly as typed, because the field is meant for short codes, and finding out which unknown codes people try (old campaigns, typos) is the whole point of that event.

&nbsp;

## What was left out on purpose

A few things that might seem obvious were deliberately left out:

- **No `:core:analytics` module.** The list of systems belongs to this app, so events and `Analytics` live in `:shared:analytics`; what would be left for `core` is too small to justify a module.
- **No `Analytics` interface.** There is only one implementation; tests give it `TestAnalyticsClient` instances, which record what is sent.
- **No event roles or labels** (product, marketing, ads): an event lists its systems directly.
