package krio.systemdesign.shoppingapp.core.ui.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.R

// Кнопка «Наверх» над длинным списком. Появляется и исчезает плавно, когда меняется visible.
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
        // Высотой как кнопка «В корзину» на карточках (CartQuantityControl); с крупным шрифтом растёт вместе с текстом.
        WithoutTouchTargetReserve {
            ExtendedFloatingActionButton(
                onClick = onClick,
                modifier = Modifier.heightIn(min = ButtonDefaults.MinHeight),
            ) {
                Text(stringResource(R.string.core_ui_scroll_to_top))
            }
        }
    }
}
