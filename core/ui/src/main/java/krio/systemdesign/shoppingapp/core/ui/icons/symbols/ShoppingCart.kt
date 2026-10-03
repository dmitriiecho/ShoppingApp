package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val MaterialSymbols.ShoppingCart: ImageVector by lazy {
    ImageVector.Builder(
        name = "ShoppingCart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 800f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 720f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(360f, 800f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(280f, 880f)
            close()
            moveToRelative(400f, 0f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(600f, 800f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(680f, 720f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 800f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 880f)
            close()
            moveTo(246f, 240f)
            lineToRelative(96f, 200f)
            horizontalLineToRelative(280f)
            lineToRelative(110f, -200f)
            horizontalLineTo(246f)
            close()
            moveToRelative(-38f, -80f)
            horizontalLineToRelative(590f)
            quadToRelative(23f, 0f, 35f, 20.5f)
            reflectiveQuadToRelative(1f, 41.5f)
            lineTo(692f, 478f)
            quadToRelative(-11f, 20f, -29.5f, 31f)
            reflectiveQuadTo(622f, 520f)
            horizontalLineTo(324f)
            lineToRelative(-44f, 80f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(80f)
            horizontalLineTo(280f)
            quadToRelative(-45f, 0f, -68f, -39.5f)
            reflectiveQuadToRelative(-2f, -78.5f)
            lineToRelative(54f, -98f)
            lineToRelative(-144f, -304f)
            horizontalLineTo(40f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(130f)
            lineToRelative(38f, 80f)
            close()
            moveToRelative(134f, 280f)
            horizontalLineToRelative(280f)
            horizontalLineToRelative(-280f)
            close()
        }
    }.build()
}
