package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// The app's Material color scheme: one constant per role in each theme, as in a Material Theme Builder export.
// App colors beyond the scheme are in AppColors.kt.
// Palette colors are visible only inside :core:ui: screens take colors by meaning,
// from MaterialTheme.colorScheme and ShoppingAppTheme.colors, not from here.

// Light theme
internal val PrimaryLight = Color(0xFFB3532A)
internal val OnPrimaryLight = Color(0xFFFFFFFF)
internal val PrimaryContainerLight = Color(0xFFF6DDCF)
internal val OnPrimaryContainerLight = Color(0xFF4E1D08)
internal val InversePrimaryLight = Color(0xFFE39565)

internal val SecondaryLight = Color(0xFF77574A)
internal val OnSecondaryLight = Color(0xFFFFFFFF)
internal val SecondaryContainerLight = Color(0xFFFBE3D6)
internal val OnSecondaryContainerLight = Color(0xFF2C160D)

internal val TertiaryLight = Color(0xFF5E6136)
internal val OnTertiaryLight = Color(0xFFFFFFFF)
internal val TertiaryContainerLight = Color(0xFFE4E6AF)
internal val OnTertiaryContainerLight = Color(0xFF1B1D00)

internal val ErrorLight = Color(0xFFD0183F)
internal val OnErrorLight = Color(0xFFFFFFFF)
internal val ErrorContainerLight = Color(0xFFFFD9DF)
internal val OnErrorContainerLight = Color(0xFF40000F)

internal val BackgroundLight = Color(0xFFF6F4F2)
internal val OnBackgroundLight = Color(0xFF201A17)
internal val SurfaceLight = Color(0xFFF6F4F2)
internal val OnSurfaceLight = Color(0xFF201A17)
internal val SurfaceVariantLight = Color(0xFFF2DFD5)
internal val OnSurfaceVariantLight = Color(0xFF53443C)
internal val SurfaceTintLight = Color(0xFFB3532A)
internal val InverseSurfaceLight = Color(0xFF362F2B)
internal val InverseOnSurfaceLight = Color(0xFFFBEEE8)

internal val OutlineLight = Color(0xFF85736B)
internal val OutlineVariantLight = Color(0xFFD8C2B9)

internal val SurfaceBrightLight = Color(0xFFFBF9F7)
internal val SurfaceDimLight = Color(0xFFDDD8D4)
internal val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
internal val SurfaceContainerLowLight = Color(0xFFF2EFEC)
internal val SurfaceContainerLight = Color(0xFFECE8E5)
internal val SurfaceContainerHighLight = Color(0xFFE6E2DE)
internal val SurfaceContainerHighestLight = Color(0xFFE0DCD8)

// Dark theme
internal val PrimaryDark = Color(0xFFE39565)
internal val OnPrimaryDark = Color(0xFF3A1A08)
internal val PrimaryContainerDark = Color(0xFF6A3519)
internal val OnPrimaryContainerDark = Color(0xFFF6DDCF)
internal val InversePrimaryDark = Color(0xFFB3532A)

internal val SecondaryDark = Color(0xFFE7BDAA)
internal val OnSecondaryDark = Color(0xFF442A1E)
internal val SecondaryContainerDark = Color(0xFF5D4033)
internal val OnSecondaryContainerDark = Color(0xFFFFDBCC)

internal val TertiaryDark = Color(0xFFC8CA95)
internal val OnTertiaryDark = Color(0xFF30330B)
internal val TertiaryContainerDark = Color(0xFF464920)
internal val OnTertiaryContainerDark = Color(0xFFE4E6AF)

internal val ErrorDark = Color(0xFFFF4D67)
internal val OnErrorDark = Color(0xFF3B0010)
internal val ErrorContainerDark = Color(0xFF8E0A2B)
internal val OnErrorContainerDark = Color(0xFFFFD9DF)

internal val BackgroundDark = Color(0xFF131315)
internal val OnBackgroundDark = Color(0xFFE6E5E8)
internal val SurfaceDark = Color(0xFF131315)
internal val OnSurfaceDark = Color(0xFFE6E5E8)
internal val SurfaceVariantDark = Color(0xFF45464A)
internal val OnSurfaceVariantDark = Color(0xFFC7C6CB)
internal val SurfaceTintDark = Color(0xFFE39565)
internal val InverseSurfaceDark = Color(0xFFE6E5E8)
internal val InverseOnSurfaceDark = Color(0xFF303033)

internal val OutlineDark = Color(0xFF919096)
internal val OutlineVariantDark = Color(0xFF45464A)

internal val SurfaceBrightDark = Color(0xFF3A3A3E)
internal val SurfaceDimDark = Color(0xFF131315)
internal val SurfaceContainerLowestDark = Color(0xFF0E0E10)
internal val SurfaceContainerLowDark = Color(0xFF1B1B1E)
internal val SurfaceContainerDark = Color(0xFF202023)
internal val SurfaceContainerHighDark = Color(0xFF2A2A2E)
internal val SurfaceContainerHighestDark = Color(0xFF353539)

// The schemes use the app's own palette, not one from the wallpaper (dynamic color on Android 12+):
// the app looks the same on every device, and colors stay saturated and contrasting.
internal val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = SurfaceTintLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    surfaceBright = SurfaceBrightLight,
    surfaceDim = SurfaceDimLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

internal val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = SurfaceTintDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    surfaceBright = SurfaceBrightDark,
    surfaceDim = SurfaceDimDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)
