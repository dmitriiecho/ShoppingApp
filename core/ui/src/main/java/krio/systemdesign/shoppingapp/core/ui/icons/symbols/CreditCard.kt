package krio.systemdesign.shoppingapp.core.ui.icons.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbols.CreditCard: ImageVector by lazy {
    ImageVector.Builder(
        name = "CreditCard",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(880f, 240f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            horizontalLineTo(160f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            close()
            moveToRelative(-720f, 80f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-80f)
            horizontalLineTo(160f)
            verticalLineToRelative(80f)
            close()
            moveToRelative(0f, 160f)
            verticalLineToRelative(240f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-240f)
            horizontalLineTo(160f)
            close()
            moveToRelative(0f, 240f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
        }
    }.build()
}
