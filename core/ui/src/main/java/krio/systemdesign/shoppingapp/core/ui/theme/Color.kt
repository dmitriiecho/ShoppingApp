package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// Своя палитра приложения: приглушённый оранжевый (терракотовый) акцент и тёплые нейтральные цвета.
// Акцент в светлой теме темнее, чем в тёмной: на белом у него контраст 5:1, белый текст на нём читается.

// Светлая тема
val OrangeLight = Color(0xFFB3532A)
val OrangeContainerLight = Color(0xFFF6DDCF)
val OnOrangeContainerLight = Color(0xFF4E1D08)
val SecondaryLight = Color(0xFF77574A)
val SecondaryContainerLight = Color(0xFFFBE3D6)
val OnSecondaryContainerLight = Color(0xFF2C160D)
val TertiaryLight = Color(0xFF5E6136)
val TertiaryContainerLight = Color(0xFFE4E6AF)
val OnTertiaryContainerLight = Color(0xFF1B1D00)
val BackgroundLight = Color(0xFFF6F4F2)
val OnBackgroundLight = Color(0xFF201A17)
val SurfaceVariantLight = Color(0xFFF2DFD5)
val OnSurfaceVariantLight = Color(0xFF53443C)
val OutlineLight = Color(0xFF85736B)
val OutlineVariantLight = Color(0xFFD8C2B9)
val SurfaceContainerLowLight = Color(0xFFF2EFEC)
val SurfaceContainerLight = Color(0xFFECE8E5)
val SurfaceContainerHighLight = Color(0xFFE6E2DE)
val SurfaceContainerHighestLight = Color(0xFFE0DCD8)
val SurfaceBrightLight = Color(0xFFFBF9F7)
val SurfaceDimLight = Color(0xFFDDD8D4)
val InverseSurfaceLight = Color(0xFF362F2B)
val InverseOnSurfaceLight = Color(0xFFFBEEE8)

// Тёмная тема
val OrangeDark = Color(0xFFE39565)
val OnOrangeDark = Color(0xFF3A1A08)
val OrangeContainerDark = Color(0xFF6A3519)
val OnOrangeContainerDark = Color(0xFFF6DDCF)
val SecondaryDark = Color(0xFFE7BDAA)
val OnSecondaryDark = Color(0xFF442A1E)
val SecondaryContainerDark = Color(0xFF5D4033)
val OnSecondaryContainerDark = Color(0xFFFFDBCC)
val TertiaryDark = Color(0xFFC8CA95)
val OnTertiaryDark = Color(0xFF30330B)
val TertiaryContainerDark = Color(0xFF464920)
val OnTertiaryContainerDark = Color(0xFFE4E6AF)
// Фоны тёмной темы нейтральные графитовые: тёплые (с коричневой примесью) на тёмном выглядят желтоватыми.
val BackgroundDark = Color(0xFF131315)
val OnBackgroundDark = Color(0xFFE6E5E8)
val SurfaceVariantDark = Color(0xFF45464A)
val OnSurfaceVariantDark = Color(0xFFC7C6CB)
val OutlineDark = Color(0xFF919096)
val OutlineVariantDark = Color(0xFF45464A)
val SurfaceContainerLowestDark = Color(0xFF0E0E10)
val SurfaceContainerLowDark = Color(0xFF1B1B1E)
val SurfaceContainerDark = Color(0xFF202023)
val SurfaceContainerHighDark = Color(0xFF2A2A2E)
val SurfaceContainerHighestDark = Color(0xFF353539)
val SurfaceBrightDark = Color(0xFF3A3A3E)
val InverseSurfaceDark = Color(0xFFE6E5E8)
val InverseOnSurfaceDark = Color(0xFF303033)

// Цвет ошибок и предупреждений (иконка очистки корзины, счётчик на вкладке, плашки «закончился»).
// Малиновый, а не стандартный красный Material: тот почти того же тона, что оранжевый акцент, и сливается с ним,
// а в тёмной теме стандартный слишком бледный.
val ErrorLight = Color(0xFFD0183F)
val ErrorContainerLight = Color(0xFFFFD9DF)
val OnErrorContainerLight = Color(0xFF40000F)
val ErrorDark = Color(0xFFFF4D67)
val OnErrorDark = Color(0xFF3B0010)
val ErrorContainerDark = Color(0xFF8E0A2B)
val OnErrorContainerDark = Color(0xFFFFD9DF)

val Green80 = Color(0xFF8BD69B)
val Green40 = Color(0xFF2E7D32)

// Зелёный для «всё в порядке», например для действующего промокода: в схеме Material такого цвета нет.
// Тёмная тема или светлая, определяется по фону самой схемы.
val ColorScheme.success: Color
    get() = if (background.luminance() < 0.5f) Green80 else Green40
