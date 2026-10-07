package krio.systemdesign.shoppingapp.shared.analytics.event

// Explicit values, so renaming the code doesn't change what analytics receives.
enum class AnalyticsScreen(val value: String) {
    CatalogList("catalog_list"),
    ProductDetails("product_details"),
    Cart("cart"),
    PromoCode("promo_code"),
    Checkout("checkout"),
    Settings("settings"),
}
