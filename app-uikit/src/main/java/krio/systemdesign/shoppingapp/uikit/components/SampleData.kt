package krio.systemdesign.shoppingapp.uikit.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import krio.systemdesign.shoppingapp.uikit.R

// Данные для примеров. Цены в центах, как во всём приложении.
object SampleData {
    const val HEADPHONES_PRICE = 14_999L
    const val KEYBOARD_PRICE = 10_995L
    const val PROMO_CODE = "SALE10"
    const val PROMO_DISCOUNT_PERCENT = 10
    const val SECOND_PROMO_CODE = "SALE25"
    const val SECOND_PROMO_DISCOUNT_PERCENT = 25

    // Сколько длится загрузка в «живых» примерах, например после нажатия LoadingButton.
    const val LOADING_MILLIS = 2_000L

    // Варианты задержки сети в секундах, как на экране настроек.
    val DELAY_SECONDS = listOf(0, 2, 4)

    val headphonesImageUrl: String
        @Composable get() = resourceImageUrl(R.drawable.sample_product_headphones)

    val keyboardImageUrl: String
        @Composable get() = resourceImageUrl(R.drawable.sample_product_keyboard)

    // Адрес картинки, которой нет: так ProductImage показывает значок «картинки нет».
    val missingImageUrl: String
        @Composable get() = "android.resource://${LocalContext.current.packageName}/drawable/missing_image"
}

// Картинки товаров лежат в ресурсах каталога, а не на сервере: примеры работают без сети.
@Composable
private fun resourceImageUrl(@DrawableRes id: Int): String =
    "android.resource://${LocalContext.current.packageName}/$id"
