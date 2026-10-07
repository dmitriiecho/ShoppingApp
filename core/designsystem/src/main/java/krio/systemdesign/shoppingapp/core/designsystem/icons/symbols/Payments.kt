package krio.systemdesign.shoppingapp.core.designsystem.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons

val AppIcons.Payments: ImageVector by lazy {
    ImageVector.Builder(
        name = "Payments",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(560f, 520f)
            quadToRelative(-50f, 0f, -85f, -35f)
            reflectiveQuadToRelative(-35f, -85f)
            quadToRelative(0f, -50f, 35f, -85f)
            reflectiveQuadToRelative(85f, -35f)
            quadToRelative(50f, 0f, 85f, 35f)
            reflectiveQuadToRelative(35f, 85f)
            quadToRelative(0f, 50f, -35f, 85f)
            reflectiveQuadToRelative(-85f, 35f)
            close()
            moveTo(280f, 640f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 560f)
            verticalLineToRelative(-320f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 160f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(920f, 240f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(840f, 640f)
            horizontalLineTo(280f)
            close()
            moveToRelative(80f, -80f)
            horizontalLineToRelative(400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(840f, 480f)
            verticalLineToRelative(-160f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(760f, 240f)
            horizontalLineTo(360f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(280f, 320f)
            verticalLineToRelative(160f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(360f, 560f)
            close()
            moveToRelative(440f, 240f)
            horizontalLineTo(120f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(40f, 720f)
            verticalLineToRelative(-440f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(440f)
            horizontalLineToRelative(680f)
            verticalLineToRelative(80f)
            close()
            moveTo(280f, 560f)
            verticalLineToRelative(-320f)
            verticalLineToRelative(320f)
            close()
        }
    }.build()
}
