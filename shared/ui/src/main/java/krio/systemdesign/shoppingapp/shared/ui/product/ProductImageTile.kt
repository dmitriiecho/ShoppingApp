package krio.systemdesign.shoppingapp.shared.ui.product

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

// The tile under the product image: a light spot in the center and slightly tinted edges, like a studio photo.
@Composable
@ReadOnlyComposable
internal fun productImageTile(): Brush {
    val scheme = MaterialTheme.colorScheme
    val (center, edge) = if (ShoppingAppTheme.isDark) {
        // The whole tile is lighter than the card and the center noticeably so: otherwise the tile gets lost
        // on the card and black products (headphones, keyboard) blend into it.
        lerp(scheme.surfaceContainerHighest, Color.White, 0.30f) to
            lerp(lerp(scheme.surfaceContainerHighest, Color.White, 0.06f), scheme.primary, 0.03f)
    } else {
        // A white center and edges with a hint of the accent.
        Color.White to lerp(scheme.surfaceContainerHigh, scheme.primary, 0.04f)
    }
    return Brush.radialGradient(listOf(center, edge))
}
