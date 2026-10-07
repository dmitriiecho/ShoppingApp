package krio.systemdesign.shoppingapp.uikit.samples

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import krio.systemdesign.shoppingapp.uikit.R

// Sample data. Prices are in cents, as everywhere in the app.
internal object SampleData {
    const val HEADPHONES_PRICE = 14_999L
    const val KEYBOARD_PRICE = 10_995L
    const val HUB_PRICE = 2_999L
    const val CUTTING_BOARD_OLD_PRICE = 4_900L
    const val CUTTING_BOARD_PRICE = 5_400L
    const val NOTEBOOK_OLD_PRICE = 995L
    const val NOTEBOOK_PRICE = 1_295L
    const val PROMO_CODE = "SALE10"
    const val PROMO_DISCOUNT_PERCENT = 10
    const val SECOND_PROMO_CODE = "SALE25"
    const val SECOND_PROMO_DISCOUNT_PERCENT = 25

    // How long a fake load in an interactive sample takes.
    const val LOADING_MILLIS = 2_000L

    val headphonesImageUrl: String
        @Composable @ReadOnlyComposable
        get() = resourceImageUrl(R.drawable.sample_product_headphones)

    val keyboardImageUrl: String
        @Composable @ReadOnlyComposable
        get() = resourceImageUrl(R.drawable.sample_product_keyboard)

    // An image that doesn't exist, so ProductImage shows its "no image" placeholder.
    val missingImageUrl: String
        @Composable @ReadOnlyComposable
        get() = "android.resource://${LocalContext.current.packageName}/drawable/missing_image"
}

// Product images are UI kit resources, not server URLs, so the samples work offline.
@Composable
@ReadOnlyComposable
private fun resourceImageUrl(@DrawableRes id: Int): String =
    "android.resource://${LocalContext.current.packageName}/$id"
