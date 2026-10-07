# Analytics

[Русская версия](../ru/analytics.md) · [All pages](README.md)

The app sends events to two analytics systems. Both are fakes that write to logcat, but the code is shaped as it would be with real SDKs.

## How an event travels

```text
ViewModel ──log(event)──▶ Analytics ──▶ a client for each system the event names ──▶ the system's SDK
```

| Part | Where | What it does |
|---|---|---|
| `AnalyticsEvent` | `:shared:analytics` | a name, typed params and the systems it goes to |
| `Analytics` | `:shared:analytics` | passes an event to the clients of its systems |
| `AnalyticsSystem` | `:shared:analytics` | the systems the app uses: `Insights` (product analytics), `AdTracker` (ads) |
| `AnalyticsSystemClient` | `:shared:analytics` | code that sends to one system |
| `FakeInsightsClient`, `FakeAdTrackerClient` | `:apps:shop` (`analytics/`) | one client per system; they stand in for SDKs such as Firebase Analytics and AppsFlyer |

## Events

Each event is a class that names its systems:

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

- **The event decides where it goes.** In practice events are added often and systems change rarely, and a tracking plan routes each event; a routing table in the app was tried and dropped.
- **Params are typed** (`AnalyticsValue`: text, number, flag), only types every system accepts. No `Map<String, Any>`.
- **Names are explicit strings**, so renaming code doesn't change what the systems receive.
- **An event lives in the feature that sends it** (`impl/…/analytics/`), or in `:shared:analytics` (`event/<topic>/`) when two or more features send it.

## When events are sent

- **From ViewModels only, after the action succeeds**: "added to cart" is logged once Room has saved the item, not on the tap.
- **Screen views come from the app**, not from screens ([`ScreenViews.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/ScreenViews.kt)). It watches the back stack and reports the screen on top after each navigation and each return to the foreground. Each feature names its screens in `NavDestination.xxxAnalyticsScreen()`; a screen without a name isn't reported.

## Clients and failures

- **Exactly one client per system**, checked when `Analytics` is created: otherwise a system without a client would lose events silently, and a system with two would get them twice.
- **A failing client doesn't stop the others**: its exception is logged and the event still goes to the other systems.

## What may be sent

- **No free text the user typed.** Search sends only the query's length: a search field can hold anything, a name or a phone number.
- **Except rejected promo codes**: the code is sent as typed, because the field is meant for short codes, and which unknown codes people try (old campaigns, typos) is what that event is for.

## What was left out on purpose

- **No `:core:analytics` module.** The list of systems belongs to this app, so events and `Analytics` live in `:shared:analytics`; what would stay in `core` is too small for a module.
- **No `Analytics` interface.** There is one implementation; a test can pass a recording client instead.
- **No event roles or labels** (product, marketing, ads): an event names its systems directly.
