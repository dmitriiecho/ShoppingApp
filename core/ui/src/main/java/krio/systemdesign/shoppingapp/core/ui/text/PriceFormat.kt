package krio.systemdesign.shoppingapp.core.ui.text

import java.text.NumberFormat
import java.util.Locale

// Цена в копейках → «1 234,56 ₽».
fun formatPrice(amountMinor: Long): String {
    val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "${format.format(amountMinor / 100.0)} ₽"
}
