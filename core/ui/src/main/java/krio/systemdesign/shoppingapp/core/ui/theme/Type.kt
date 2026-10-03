package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Material 3 scale with the system font. bodyLarge has no LineHeightStyle: card paddings are tuned for that.
internal val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)

// Bold bodyLarge, e.g. "Total" in OrderTotals. Not named bodyLargeEmphasized: newer Material 3 has its own.
val Typography.bodyLargeStrong: TextStyle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold)
