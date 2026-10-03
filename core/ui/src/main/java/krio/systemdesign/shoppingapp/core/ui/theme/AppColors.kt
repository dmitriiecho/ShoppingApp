package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Цвета приложения по смыслу, которых нет в схеме Material (MaterialTheme.colorScheme).
// Здесь только то, что нужно разным компонентам; цвета одного компонента вычисляются рядом с ним из схемы,
// как у компонентов Material. Светлый или тёмный набор выбирает ShoppingAppTheme. Берутся через ShoppingAppTheme.colors.
@Immutable
class AppColors(
    // «Всё в порядке», например действующий промокод.
    val success: Color,
    // Фон карточек (AppCard). В светлой теме белый на сером фоне страницы, в тёмной светлее фона:
    // так карточки не сливаются со страницей. У стандартной Card фон surfaceContainerHighest —
    // в светлой теме это серый, почти как фон страницы.
    val cardContainer: Color,
)

// Зелёный для success.
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

// Без ShoppingAppTheme (например, в чужой теме) — светлый набор.
internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }
