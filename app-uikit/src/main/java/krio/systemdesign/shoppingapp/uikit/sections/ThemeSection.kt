package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.core.ui.theme.bodyLargeStrong
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

// Цвета и шрифты темы. Всё берётся из MaterialTheme, поэтому кнопка темы в верхней панели показывает обе палитры.
@Composable
fun ThemeSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("MaterialTheme.colorScheme") {
            SampleVariant {
                colorRoles().forEach { ColorSwatch(it) }
            }
        }
        sampleGroup("ShoppingAppTheme.colors") {
            SampleVariant {
                appColorRoles().forEach { ColorSwatch(it) }
            }
        }
        sampleGroup("MaterialTheme.typography") {
            SampleVariant {
                typographyStyles(MaterialTheme.typography).forEach { (name, style) ->
                    Text(text = name, style = style)
                }
            }
        }
        sampleGroup("MaterialTheme.shapes · ShapeRadius") {
            SampleVariant {
                shapeSamples(MaterialTheme.shapes).forEach { ShapeSwatch(it) }
            }
        }
        sampleGroup("Spacing") {
            SampleVariant {
                spacingSamples().forEach { (name, value) -> SpacingSwatch(name, value) }
            }
        }
    }
}

// contentColor — цвет текста на этом фоне: парный on-цвет схемы или тот, с которым цвет встречается в приложении.
private class ColorRole(val name: String, val color: Color, val contentColor: Color)

@Composable
private fun colorRoles(): List<ColorRole> {
    val colors = MaterialTheme.colorScheme
    return listOf(
        ColorRole("primary", colors.primary, colors.onPrimary),
        ColorRole("primaryContainer", colors.primaryContainer, colors.onPrimaryContainer),
        ColorRole("secondary", colors.secondary, colors.onSecondary),
        ColorRole("secondaryContainer", colors.secondaryContainer, colors.onSecondaryContainer),
        ColorRole("tertiary", colors.tertiary, colors.onTertiary),
        ColorRole("tertiaryContainer", colors.tertiaryContainer, colors.onTertiaryContainer),
        ColorRole("error", colors.error, colors.onError),
        ColorRole("errorContainer", colors.errorContainer, colors.onErrorContainer),
        ColorRole("background", colors.background, colors.onBackground),
        ColorRole("surfaceContainerLowest", colors.surfaceContainerLowest, colors.onSurface),
        ColorRole("surfaceContainerLow", colors.surfaceContainerLow, colors.onSurface),
        ColorRole("surfaceContainer", colors.surfaceContainer, colors.onSurface),
        ColorRole("surfaceContainerHigh", colors.surfaceContainerHigh, colors.onSurface),
        ColorRole("surfaceContainerHighest", colors.surfaceContainerHighest, colors.onSurface),
        ColorRole("onSurfaceVariant", colors.onSurfaceVariant, colors.surface),
        ColorRole("outline", colors.outline, colors.surface),
        ColorRole("outlineVariant", colors.outlineVariant, colors.onSurface),
        ColorRole("inverseSurface", colors.inverseSurface, colors.inverseOnSurface),
    )
}

// Цвета сверх схемы Material. Своих on-цветов у них нет: текст на образце — цвет, с которым они встречаются в приложении.
@Composable
private fun appColorRoles(): List<ColorRole> {
    val colors = MaterialTheme.colorScheme
    val appColors = ShoppingAppTheme.colors
    return listOf(
        // В приложении success — цвет текста и значка на светлом оттенке самого себя.
        ColorRole("success", appColors.success, colors.background),
        ColorRole("cardContainer", appColors.cardContainer, colors.onSurface),
    )
}

// Образец цвета: название роли и её значение. Рамка — чтобы фоновые цвета не сливались со страницей.
@Composable
private fun ColorSwatch(role: ColorRole) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = role.color,
        contentColor = role.contentColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = role.name,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = role.color.toHex(),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

private fun Color.toHex(): String = "#%06X".format(toArgb() and 0xFFFFFF)

private fun typographyStyles(typography: Typography): List<Pair<String, TextStyle>> = listOf(
    "displayLarge" to typography.displayLarge,
    "displayMedium" to typography.displayMedium,
    "displaySmall" to typography.displaySmall,
    "headlineLarge" to typography.headlineLarge,
    "headlineMedium" to typography.headlineMedium,
    "headlineSmall" to typography.headlineSmall,
    "titleLarge" to typography.titleLarge,
    "titleMedium" to typography.titleMedium,
    "titleSmall" to typography.titleSmall,
    "bodyLarge" to typography.bodyLarge,
    "bodyLargeStrong" to typography.bodyLargeStrong,
    "bodyMedium" to typography.bodyMedium,
    "bodySmall" to typography.bodySmall,
    "labelLarge" to typography.labelLarge,
    "labelMedium" to typography.labelMedium,
    "labelSmall" to typography.labelSmall,
)

private class ShapeSample(val name: String, val shape: Shape, val radius: Dp)

private fun shapeSamples(shapes: Shapes): List<ShapeSample> = listOf(
    ShapeSample("extraSmall", shapes.extraSmall, ShapeRadius.ExtraSmall),
    ShapeSample("small", shapes.small, ShapeRadius.Small),
    ShapeSample("medium", shapes.medium, ShapeRadius.Medium),
    ShapeSample("large", shapes.large, ShapeRadius.Large),
    ShapeSample("extraLarge", shapes.extraLarge, ShapeRadius.ExtraLarge),
)

@Composable
private fun ShapeSwatch(sample: ShapeSample) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = sample.shape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {}
        Spacer(Modifier.width(12.dp))
        SwatchLabel(sample.name, sample.radius)
    }
}

private fun spacingSamples(): List<Pair<String, Dp>> = listOf(
    "ScreenPadding" to Spacing.ScreenPadding,
    "CardSpacing" to Spacing.CardSpacing,
    "SectionSpacing" to Spacing.SectionSpacing,
    "CardPadding" to Spacing.CardPadding,
)

// The bar is as wide as the spacing.
@Composable
private fun SpacingSwatch(name: String, value: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = value, height = 24.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.width(12.dp))
        SwatchLabel(name, value)
    }
}

@Composable
private fun SwatchLabel(name: String, value: Dp) {
    Text(
        text = "$name · ${value.value.toInt()} dp",
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = FontFamily.Monospace,
    )
}

