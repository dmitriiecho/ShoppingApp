package krio.systemdesign.shoppingapp.crossfeature.ui.text

import kotlin.math.absoluteValue

// Cents as "$1,234.56" in US format. Prices are USD, so it ignores the phone language. Written by hand: the
// platforms' number formats aren't available in common code, and this one format is all the app needs.
fun formatPrice(amountCents: Long): String {
    val sign = if (amountCents < 0) "-" else ""
    val cents = amountCents.absoluteValue
    val dollars = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val remainder = (cents % 100).toString().padStart(2, '0')
    return "$sign$$dollars.$remainder"
}
