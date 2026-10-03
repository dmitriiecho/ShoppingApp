package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbols.HomeFilled: ImageVector by lazy {
    ImageVector.Builder(
        name = "HomeFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 840f)
            verticalLineToRelative(-480f)
            lineToRelative(320f, -240f)
            lineToRelative(320f, 240f)
            verticalLineToRelative(480f)
            horizontalLineTo(560f)
            verticalLineToRelative(-280f)
            horizontalLineTo(400f)
            verticalLineToRelative(280f)
            horizontalLineTo(160f)
            close()
        }
    }.build()
}
