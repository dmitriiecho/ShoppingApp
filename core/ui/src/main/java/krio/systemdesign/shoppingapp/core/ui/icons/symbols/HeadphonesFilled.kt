package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons

val AppIcons.HeadphonesFilled: ImageVector by lazy {
    ImageVector.Builder(
        name = "HeadphonesFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(360f, 840f)
            horizontalLineTo(200f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-280f)
            quadToRelative(0f, -75f, 28.5f, -140.5f)
            reflectiveQuadToRelative(77f, -114f)
            quadToRelative(48.5f, -48.5f, 114f, -77f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(75f, 0f, 140.5f, 28.5f)
            reflectiveQuadToRelative(114f, 77f)
            quadToRelative(48.5f, 48.5f, 77f, 114f)
            reflectiveQuadTo(840f, 480f)
            verticalLineToRelative(280f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            horizontalLineTo(600f)
            verticalLineToRelative(-320f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -117f, -81.5f, -198.5f)
            reflectiveQuadTo(480f, 200f)
            quadToRelative(-117f, 0f, -198.5f, 81.5f)
            reflectiveQuadTo(200f, 480f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(320f)
            close()
        }
    }.build()
}
