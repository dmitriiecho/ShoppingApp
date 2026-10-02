package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.ui.graphics.Color

// Своя палитра приложения: приглушённый оранжевый (терракотовый) акцент и тёплые нейтральные цвета.
// Акцент в светлой теме темнее, чем в тёмной: на белом у него контраст 5:1, белый текст на нём читается.
// Цвета палитры видны только внутри :core:ui: экраны берут цвета по смыслу, из MaterialTheme.colorScheme
// и ShoppingAppTheme.colors, а не отсюда.

// Светлая тема
internal val OrangeLight = Color(0xFFB3532A)
internal val OrangeContainerLight = Color(0xFFF6DDCF)
internal val OnOrangeContainerLight = Color(0xFF4E1D08)
internal val SecondaryLight = Color(0xFF77574A)
internal val SecondaryContainerLight = Color(0xFFFBE3D6)
internal val OnSecondaryContainerLight = Color(0xFF2C160D)
internal val TertiaryLight = Color(0xFF5E6136)
internal val TertiaryContainerLight = Color(0xFFE4E6AF)
internal val OnTertiaryContainerLight = Color(0xFF1B1D00)
internal val BackgroundLight = Color(0xFFF6F4F2)
internal val OnBackgroundLight = Color(0xFF201A17)
internal val SurfaceVariantLight = Color(0xFFF2DFD5)
internal val OnSurfaceVariantLight = Color(0xFF53443C)
internal val OutlineLight = Color(0xFF85736B)
internal val OutlineVariantLight = Color(0xFFD8C2B9)
internal val SurfaceContainerLowLight = Color(0xFFF2EFEC)
internal val SurfaceContainerLight = Color(0xFFECE8E5)
internal val SurfaceContainerHighLight = Color(0xFFE6E2DE)
internal val SurfaceContainerHighestLight = Color(0xFFE0DCD8)
internal val SurfaceBrightLight = Color(0xFFFBF9F7)
internal val SurfaceDimLight = Color(0xFFDDD8D4)
internal val InverseSurfaceLight = Color(0xFF362F2B)
internal val InverseOnSurfaceLight = Color(0xFFFBEEE8)

// Тёмная тема
internal val OrangeDark = Color(0xFFE39565)
internal val OnOrangeDark = Color(0xFF3A1A08)
internal val OrangeContainerDark = Color(0xFF6A3519)
internal val OnOrangeContainerDark = Color(0xFFF6DDCF)
internal val SecondaryDark = Color(0xFFE7BDAA)
internal val OnSecondaryDark = Color(0xFF442A1E)
internal val SecondaryContainerDark = Color(0xFF5D4033)
internal val OnSecondaryContainerDark = Color(0xFFFFDBCC)
internal val TertiaryDark = Color(0xFFC8CA95)
internal val OnTertiaryDark = Color(0xFF30330B)
internal val TertiaryContainerDark = Color(0xFF464920)
internal val OnTertiaryContainerDark = Color(0xFFE4E6AF)
// Фоны тёмной темы нейтральные графитовые: тёплые (с коричневой примесью) на тёмном выглядят желтоватыми.
internal val BackgroundDark = Color(0xFF131315)
internal val OnBackgroundDark = Color(0xFFE6E5E8)
internal val SurfaceVariantDark = Color(0xFF45464A)
internal val OnSurfaceVariantDark = Color(0xFFC7C6CB)
internal val OutlineDark = Color(0xFF919096)
internal val OutlineVariantDark = Color(0xFF45464A)
internal val SurfaceContainerLowestDark = Color(0xFF0E0E10)
internal val SurfaceContainerLowDark = Color(0xFF1B1B1E)
internal val SurfaceContainerDark = Color(0xFF202023)
internal val SurfaceContainerHighDark = Color(0xFF2A2A2E)
internal val SurfaceContainerHighestDark = Color(0xFF353539)
internal val SurfaceBrightDark = Color(0xFF3A3A3E)
internal val InverseSurfaceDark = Color(0xFFE6E5E8)
internal val InverseOnSurfaceDark = Color(0xFF303033)

// Цвет ошибок и предупреждений (иконка очистки корзины, счётчик на вкладке, плашки «закончился»).
// Малиновый, а не стандартный красный Material: тот почти того же тона, что оранжевый акцент, и сливается с ним,
// а в тёмной теме стандартный слишком бледный.
internal val ErrorLight = Color(0xFFD0183F)
internal val ErrorContainerLight = Color(0xFFFFD9DF)
internal val OnErrorContainerLight = Color(0xFF40000F)
internal val ErrorDark = Color(0xFFFF4D67)
internal val OnErrorDark = Color(0xFF3B0010)
internal val ErrorContainerDark = Color(0xFF8E0A2B)
internal val OnErrorContainerDark = Color(0xFFFFD9DF)

// Зелёный для «всё в порядке», например для действующего промокода: в схеме Material такого цвета нет,
// поэтому он в AppColors.
internal val SuccessLight = Color(0xFF2E7D32)
internal val SuccessDark = Color(0xFF8BD69B)
