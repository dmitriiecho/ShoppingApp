package krio.systemdesign.shoppingapp.core.designsystem.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons

val AppIcons.OpenInNew: ImageVector by lazy {
    ImageVector.Builder(
        name = "OpenInNew",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(80f)
            horizontalLineTo(200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-280f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(280f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            horizontalLineTo(200f)
            close()
            moveToRelative(188f, -212f)
            lineToRelative(-56f, -56f)
            lineToRelative(372f, -372f)
            horizontalLineTo(560f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(280f)
            verticalLineToRelative(280f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-144f)
            lineTo(388f, 628f)
            close()
        }
    }.build()
}
