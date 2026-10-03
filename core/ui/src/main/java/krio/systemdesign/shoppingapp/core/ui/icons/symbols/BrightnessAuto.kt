package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val MaterialSymbols.BrightnessAuto: ImageVector by lazy {
    ImageVector.Builder(
        name = "BrightnessAuto",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(312f, 640f)
            horizontalLineToRelative(64f)
            lineToRelative(32f, -92f)
            horizontalLineToRelative(146f)
            lineToRelative(32f, 92f)
            horizontalLineToRelative(62f)
            lineTo(512f, 280f)
            horizontalLineToRelative(-64f)
            lineTo(312f, 640f)
            close()
            moveToRelative(114f, -144f)
            lineToRelative(52f, -150f)
            horizontalLineToRelative(4f)
            lineToRelative(52f, 150f)
            horizontalLineTo(426f)
            close()
            moveToRelative(54f, 436f)
            lineTo(346f, 800f)
            horizontalLineTo(160f)
            verticalLineToRelative(-186f)
            lineTo(28f, 480f)
            lineToRelative(132f, -134f)
            verticalLineToRelative(-186f)
            horizontalLineToRelative(186f)
            lineToRelative(134f, -132f)
            lineToRelative(134f, 132f)
            horizontalLineToRelative(186f)
            verticalLineToRelative(186f)
            lineToRelative(132f, 134f)
            lineToRelative(-132f, 134f)
            verticalLineToRelative(186f)
            horizontalLineTo(614f)
            lineTo(480f, 932f)
            close()
            moveToRelative(0f, -112f)
            lineToRelative(100f, -100f)
            horizontalLineToRelative(140f)
            verticalLineToRelative(-140f)
            lineToRelative(100f, -100f)
            lineToRelative(-100f, -100f)
            verticalLineToRelative(-140f)
            horizontalLineTo(580f)
            lineTo(480f, 140f)
            lineTo(380f, 240f)
            horizontalLineTo(240f)
            verticalLineToRelative(140f)
            lineTo(140f, 480f)
            lineToRelative(100f, 100f)
            verticalLineToRelative(140f)
            horizontalLineToRelative(140f)
            lineToRelative(100f, 100f)
            close()
            moveToRelative(0f, -340f)
            close()
        }
    }.build()
}
