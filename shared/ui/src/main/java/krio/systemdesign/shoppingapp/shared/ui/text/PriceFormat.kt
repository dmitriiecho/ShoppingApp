package krio.systemdesign.shoppingapp.shared.ui.text

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

// Cents as "$1,234.56" in US format. Prices are USD, so it ignores the phone language.
fun formatPrice(amountCents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(BigDecimal.valueOf(amountCents, 2))
