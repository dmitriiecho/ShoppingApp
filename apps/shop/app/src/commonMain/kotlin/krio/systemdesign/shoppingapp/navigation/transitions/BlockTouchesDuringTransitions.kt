package krio.systemdesign.shoppingapp.navigation.transitions

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController

// The leaving screen still takes taps: a double tap on Back closed two screens. The top screen is RESUMED only
// once its transition ends; checked on each touch, not on recomposition, as a second tap can come first.
internal fun Modifier.blockTouchesDuringTransitions(navController: NavHostController): Modifier =
    pointerInput(navController) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val topScreenState = navController.currentBackStackEntry?.lifecycle?.currentState
                if (topScreenState != Lifecycle.State.RESUMED) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
