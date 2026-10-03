package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Radii as numbers, for code that animates them (ProductImage). Shapes come from MaterialTheme.shapes.
object ShapeRadius {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 28.dp
}

// Same values as the Material 3 defaults; set explicitly so they are visible and changed in one place.
internal val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(ShapeRadius.ExtraSmall),
    small = RoundedCornerShape(ShapeRadius.Small),
    medium = RoundedCornerShape(ShapeRadius.Medium),
    large = RoundedCornerShape(ShapeRadius.Large),
    extraLarge = RoundedCornerShape(ShapeRadius.ExtraLarge),
)
