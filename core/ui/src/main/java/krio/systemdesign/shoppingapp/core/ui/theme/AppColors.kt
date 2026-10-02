package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Цвета приложения, которых нет в схеме Material (MaterialTheme.colorScheme), — у каждого своё назначение.
// Светлый или тёмный набор выбирает ShoppingAppTheme по тому же флагу darkTheme, что и схему,
// поэтому компонентам не нужно угадывать тему по цветам. Берутся через ShoppingAppTheme.colors.
@Immutable
class AppColors(
    // Тёмная ли тема: например, ProductImage берёт по ней свою картинку «картинки нет».
    val isDark: Boolean,
    // «Всё в порядке», например действующий промокод.
    val success: Color,
    // Фон карточек (AppCard). В светлой теме белый на сером фоне страницы, в тёмной светлее фона:
    // так карточки не сливаются со страницей. У стандартной Card фон surfaceContainerHighest —
    // в светлой теме это серый, почти как фон страницы.
    val cardContainer: Color,
    // Плитка под картинкой товара (ProductImage): светлое пятно в центре и чуть тонированные края,
    // как на студийной фотографии.
    val productImageCenter: Color,
    val productImageEdge: Color,
)

internal val LightAppColors = AppColors(
    isDark = false,
    success = SuccessLight,
    cardContainer = Color.White,
    // Белый центр и края с лёгким оттенком акцента.
    productImageCenter = Color.White,
    productImageEdge = lerp(SurfaceContainerHighLight, OrangeLight, 0.04f),
)

internal val DarkAppColors = AppColors(
    isDark = true,
    success = SuccessDark,
    cardContainer = SurfaceContainerHighDark,
    // Вся плитка светлее карточки, а центр — заметно: иначе плитка теряется на карточке,
    // а чёрные товары (наушники, клавиатура) сливаются с фоном.
    productImageCenter = lerp(SurfaceContainerHighestDark, Color.White, 0.30f),
    productImageEdge = lerp(lerp(SurfaceContainerHighestDark, Color.White, 0.06f), OrangeDark, 0.03f),
)

// Без ShoppingAppTheme (например, в чужой теме) — светлый набор.
internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }
