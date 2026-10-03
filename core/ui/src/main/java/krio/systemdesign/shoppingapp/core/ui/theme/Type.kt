package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// The app's text styles: Material 3 defaults with the system font.
// bodyLarge, the main text, has no extra space above and below the lines, so card paddings are measured from the text.
internal val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)

// A line that stands out among bodyLarge lines, e.g. the order total: the same text, only bolder.
val Typography.bodyLargeStrong: TextStyle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold)
