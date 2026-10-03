package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Шрифты приложения — шкала Material 3 со шрифтом системы, своего фирменного шрифта у приложения нет.
// Своё только у bodyLarge, основного текста.
internal val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)

// Выделенная строка в блоке суммы: «Итого» и заголовок итогов корзины. Тот же bodyLarge, что у остальных строк,
// только жирнее: у другого стиля (например, titleMedium) другой межбуквенный интервал,
// и строки выглядели бы набранными разными шрифтами.
val Typography.totalsEmphasized: TextStyle
    get() = bodyLarge.copy(fontWeight = FontWeight.SemiBold)
