package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

internal data class ProductDetailsUiState(
    val productId: String,
    // Null while unknown: the product hasn't loaded and the route didn't pass it (a deep link).
    val name: String?,
    val imageUrl: String?,
    val details: Details = Details.Loading,
) {
    // What's under the image; not found and the error take the whole screen instead.
    sealed interface Details {
        data object Loading : Details

        data class Loaded(
            val price: Long,
            val description: String,
            val cartControl: CartControl,
        ) : Details

        // The link pointed to a product the server doesn't have: not a failure, retrying won't help.
        data object NotFound : Details

        data object Error : Details
    }

    // The buttons at the bottom: "Out of stock", or the cart buttons with how many are in the cart.
    sealed interface CartControl {
        data object OutOfStock : CartControl
        data class InStock(
            val quantity: Int,
            val canIncrease: Boolean,
        ) : CartControl
    }
}
