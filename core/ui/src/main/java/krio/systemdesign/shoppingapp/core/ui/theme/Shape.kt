package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Скругления углов приложения. Фигуры берутся из MaterialTheme.shapes: например, medium у карточек, плашек и полей,
// extraSmall у строк-заглушек. Радиус числом (ShapeRadius) — для тех, кому нужен радиус, а не фигура:
// ProductImage плавно меняет его, пока картинка перелетает между экранами.
object ShapeRadius {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 28.dp
}

// Значения те же, что у Material 3 по умолчанию; заданы явно, чтобы их было видно и менять в одном месте.
internal val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(ShapeRadius.ExtraSmall),
    small = RoundedCornerShape(ShapeRadius.Small),
    medium = RoundedCornerShape(ShapeRadius.Medium),
    large = RoundedCornerShape(ShapeRadius.Large),
    extraLarge = RoundedCornerShape(ShapeRadius.ExtraLarge),
)
