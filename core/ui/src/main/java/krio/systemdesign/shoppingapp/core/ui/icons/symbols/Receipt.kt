package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbols.Receipt: ImageVector by lazy {
    ImageVector.Builder(
        name = "Receipt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 880f)
            verticalLineToRelative(-800f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            lineToRelative(60f, 60f)
            lineToRelative(60f, -60f)
            verticalLineToRelative(800f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            lineToRelative(-60f, -60f)
            lineToRelative(-60f, 60f)
            close()
            moveToRelative(120f, -200f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-80f)
            horizontalLineTo(240f)
            verticalLineToRelative(80f)
            close()
            moveToRelative(0f, -160f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-80f)
            horizontalLineTo(240f)
            verticalLineToRelative(80f)
            close()
            moveToRelative(0f, -160f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-80f)
            horizontalLineTo(240f)
            verticalLineToRelative(80f)
            close()
            moveToRelative(-40f, 404f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-568f)
            horizontalLineTo(200f)
            verticalLineToRelative(568f)
            close()
            moveToRelative(0f, -568f)
            verticalLineToRelative(568f)
            verticalLineToRelative(-568f)
            close()
        }
    }.build()
}
