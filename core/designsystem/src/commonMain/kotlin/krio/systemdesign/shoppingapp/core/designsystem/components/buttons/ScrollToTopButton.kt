package krio.systemdesign.shoppingapp.core.designsystem.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.resources.Res
import krio.systemdesign.shoppingapp.core.designsystem.resources.designsystem_scroll_to_top
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        WithoutTouchTargetReserve {
            ExtendedFloatingActionButton(
                onClick = onClick,
                modifier = Modifier.heightIn(min = ButtonDefaults.MinHeight),
            ) {
                Text(stringResource(Res.string.designsystem_scroll_to_top))
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun ScrollToTopButtonPreview() {
    ShoppingAppTheme {
        Surface {
            ScrollToTopButton(
                visible = true,
                onClick = {},
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
