package krio.systemdesign.shoppingapp.core.designsystem.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons

val AppIcons.LocationOn: ImageVector by lazy {
    ImageVector.Builder(
        name = "LocationOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 480f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(560f, 400f)
            quadToRelative(0f, -33f, -23.5f, -56.5f)
            reflectiveQuadTo(480f, 320f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(400f, 400f)
            quadToRelative(0f, 33f, 23.5f, 56.5f)
            reflectiveQuadTo(480f, 480f)
            close()
            moveToRelative(0f, 294f)
            quadToRelative(122f, -112f, 181f, -203.5f)
            reflectiveQuadTo(720f, 408f)
            quadToRelative(0f, -109f, -69.5f, -178.5f)
            reflectiveQuadTo(480f, 160f)
            quadToRelative(-101f, 0f, -170.5f, 69.5f)
            reflectiveQuadTo(240f, 408f)
            quadToRelative(0f, 71f, 59f, 162.5f)
            reflectiveQuadTo(480f, 774f)
            close()
            moveToRelative(0f, 106f)
            quadTo(319f, 743f, 239.5f, 625.5f)
            reflectiveQuadTo(160f, 408f)
            quadToRelative(0f, -150f, 96.5f, -239f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(127f, 0f, 223.5f, 89f)
            reflectiveQuadTo(800f, 408f)
            quadToRelative(0f, 100f, -79.5f, 217.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveToRelative(0f, -480f)
            close()
        }
    }.build()
}
