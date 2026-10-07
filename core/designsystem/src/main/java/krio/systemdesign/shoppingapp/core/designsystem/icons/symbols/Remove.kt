package krio.systemdesign.shoppingapp.core.designsystem.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons

val AppIcons.Remove: ImageVector by lazy {
    ImageVector.Builder(
        name = "Remove",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 520f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(80f)
            horizontalLineTo(200f)
            close()
        }
    }.build()
}
