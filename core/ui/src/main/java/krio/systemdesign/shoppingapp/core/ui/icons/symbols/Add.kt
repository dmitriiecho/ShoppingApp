package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbols.Add: ImageVector by lazy {
    ImageVector.Builder(
        name = "Add",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 520f)
            horizontalLineTo(200f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(-240f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(240f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(80f)
            horizontalLineTo(520f)
            verticalLineToRelative(240f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-240f)
            close()
        }
    }.build()
}
