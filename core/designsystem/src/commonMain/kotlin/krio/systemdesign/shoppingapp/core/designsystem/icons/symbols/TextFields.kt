package krio.systemdesign.shoppingapp.core.designsystem.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons

val AppIcons.TextFields: ImageVector by lazy {
    ImageVector.Builder(
        name = "TextFields",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 800f)
            verticalLineToRelative(-520f)
            horizontalLineTo(80f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(520f)
            verticalLineToRelative(120f)
            horizontalLineTo(400f)
            verticalLineToRelative(520f)
            horizontalLineTo(280f)
            close()
            moveToRelative(360f, 0f)
            verticalLineToRelative(-320f)
            horizontalLineTo(520f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(360f)
            verticalLineToRelative(120f)
            horizontalLineTo(760f)
            verticalLineToRelative(320f)
            horizontalLineTo(640f)
            close()
        }
    }.build()
}
