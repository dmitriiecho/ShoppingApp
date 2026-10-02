package krio.systemdesign.shoppingapp.uikit.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.uikit.R

// Переключает каталог между светлой и тёмной темой. На кнопке тема, которая включится, —
// те же значки, что у выбора темы в настройках приложения.
@Composable
fun ThemeToggleButton(
    darkTheme: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        if (darkTheme) {
            Icon(AppIcons.LightTheme, contentDescription = stringResource(R.string.uikit_switch_to_light_theme))
        } else {
            Icon(AppIcons.DarkTheme, contentDescription = stringResource(R.string.uikit_switch_to_dark_theme))
        }
    }
}
