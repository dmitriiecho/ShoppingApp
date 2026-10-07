package krio.systemdesign.shoppingapp.core.designsystem.components.buttons

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

// A Material button takes 48 dp of height to be easy to tap, even though it's drawn 40 dp tall. Here it takes
// only its 40 dp so a card doesn't grow; taps still land because Compose extends small tap areas itself.
@Composable
fun WithoutTouchTargetReserve(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp, content = content)
}
