package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// App colors by meaning that the Material scheme (MaterialTheme.colorScheme) doesn't have.
// Accessed via ShoppingAppTheme.colors.
@Immutable
class AppColors(
    val success: Color,
    val cardContainer: Color,
)

internal val SuccessLight = Color(0xFF2E7D32)
internal val SuccessDark = Color(0xFF8BD69B)

internal val LightAppColors = AppColors(
    success = SuccessLight,
    cardContainer = SurfaceContainerLowestLight,
)

internal val DarkAppColors = AppColors(
    success = SuccessDark,
    cardContainer = SurfaceContainerHighDark,
)
