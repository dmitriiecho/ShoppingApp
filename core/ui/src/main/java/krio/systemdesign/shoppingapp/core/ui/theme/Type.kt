package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The app's type scale: Material 3 with the system font, the app has no brand font of its own.
// Only bodyLarge, the main text, is custom: it has no LineHeightStyle, so there is no extra space above the first line
// and below the last one. Card and section paddings are tuned for that.
internal val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)

// bodyLarge, only bolder: for a line that stands out among bodyLarge lines, e.g. "Total" in OrderTotals.
// Another style (e.g. titleMedium) has different letter spacing, and the lines would look set in different fonts.
// Newer Material 3 versions have their own bodyLargeEmphasized; this one has another name so the two don't clash.
val Typography.bodyLargeStrong: TextStyle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold)
