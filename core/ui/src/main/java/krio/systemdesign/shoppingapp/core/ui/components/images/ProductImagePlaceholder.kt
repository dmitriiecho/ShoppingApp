package krio.systemdesign.shoppingapp.core.ui.components.images

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

// The "no image" picture ProductImage shows when an image fails to load: a crossed-out landscape.
// The slash and the sun take the accent color.
@Composable
internal fun rememberProductImagePlaceholder(): Painter {
    val accent = MaterialTheme.colorScheme.primary
    val image = remember(accent) { productImagePlaceholder(accent) }
    return rememberVectorPainter(image)
}

private fun productImagePlaceholder(accent: Color): ImageVector =
    ImageVector.Builder(
        name = "ProductImagePlaceholder",
        defaultWidth = 96.dp,
        defaultHeight = 96.dp,
        viewportWidth = 1024f,
        viewportHeight = 1024f,
    ).apply {
        addPath(addPathNodes(FRAME), pathFillType = PathFillType.EvenOdd, fill = SolidColor(FrameColor))
        addPath(addPathNodes(BACK_HILL), fill = SolidColor(BackHillColor))
        addPath(addPathNodes(FRONT_HILL), fill = SolidColor(FrontHillColor))
        addPath(addPathNodes(SUN), fill = SolidColor(accent))
        addPath(
            addPathNodes(SLASH),
            stroke = SolidColor(accent),
            strokeLineWidth = 48f,
            strokeLineCap = StrokeCap.Round,
        )
    }.build()

private val FrameColor = Color(0xFF8A776E)
private val BackHillColor = Color(0xFFD2C2B8)
private val FrontHillColor = Color(0xFFB7A198)

private const val FRAME =
    "M324 260H700A64 64 0 0 1 764 324V700A64 64 0 0 1 700 764H324A64 64 0 0 1 260 700V324A64 64 0 0 1 324 260Z" +
        "M324 304H700A20 20 0 0 1 720 324V700A20 20 0 0 1 700 720H324A20 20 0 0 1 304 700V324A20 20 0 0 1 324 304Z"
private const val BACK_HILL =
    "M318 531.8C350.3 512.7 382.7 495.8 415 490C447.3 484.2 479.7 484.8 512 517.9C544.3 551 576.7 554.2 609 547.1" +
        "C641.3 540 673.7 520.8 706 496.1V674A32 32 0 0 1 674 706H350A32 32 0 0 1 318 674Z"
private const val FRONT_HILL =
    "M318 611.8C345.3 601.4 372.7 592.4 400 585.2C433.3 576.5 466.7 569.9 500 568.6C526.7 567.5 553.3 570.3 580 580.2" +
        "C600 587.6 620 607.8 640 620C653.3 628.2 666.7 634 680 632.2C685.7 631.5 691.3 628.8 697 620" +
        "C700 615.3 703 611.6 706 587.2V674A32 32 0 0 1 674 706H350A32 32 0 0 1 318 674Z"
private const val SUN = "M360 396A36 36 0 1 1 432 396A36 36 0 1 1 360 396Z"
private const val SLASH = "M254 750L770 274"
