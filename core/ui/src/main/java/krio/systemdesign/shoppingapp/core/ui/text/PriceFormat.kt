package krio.systemdesign.shoppingapp.core.ui.text

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

// Цена в центах → «$1,234.56». Все цены в приложении в долларах США, поэтому формат
// американский и не зависит от языка телефона.
fun formatPrice(amountCents: Long): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(BigDecimal.valueOf(amountCents, 2))
