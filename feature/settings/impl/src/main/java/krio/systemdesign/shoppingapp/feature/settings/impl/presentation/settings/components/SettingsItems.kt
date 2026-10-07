package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.BrightnessAuto
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Contrast
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.HourglassEmpty
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.settings.impl.R
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

@Composable
internal fun ThemeModeItem(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        icon = AppIcons.Contrast,
        title = stringResource(R.string.settings_theme),
        description = stringResource(selected.titleRes),
        modifier = modifier,
        trailing = {
            ChoiceButtons(options = ThemeMode.entries, selected = selected, onSelect = onSelect) { mode ->
                Icon(mode.icon, contentDescription = stringResource(mode.titleRes))
            }
        },
    )
}

@Composable
internal fun NetworkDelayItem(
    selected: NetworkDelay,
    onSelect: (NetworkDelay) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        icon = AppIcons.HourglassEmpty,
        title = stringResource(R.string.settings_network_delay),
        description = stringResource(selected.descriptionRes),
        modifier = modifier,
        trailing = {
            ChoiceButtons(options = NetworkDelay.entries, selected = selected, onSelect = onSelect) { delay ->
                Text(delay.duration.inWholeSeconds.toString())
            }
        },
    )
}

// One width for every row, so the switches line up under each other.
@Composable
private fun <T> ChoiceButtons(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> Unit,
) {
    SingleChoiceButtons(
        options = options,
        selected = selected,
        onSelect = onSelect,
        modifier = Modifier.width(ChoiceButtonsWidth),
        label = label,
    )
}

private val ChoiceButtonsWidth = 168.dp

private val NetworkDelay.descriptionRes: Int
    get() = when (this) {
        NetworkDelay.None -> R.string.settings_network_delay_none
        NetworkDelay.TwoSeconds -> R.string.settings_network_delay_two_seconds
        NetworkDelay.FourSeconds -> R.string.settings_network_delay_four_seconds
    }

private val ThemeMode.titleRes: Int
    get() = when (this) {
        ThemeMode.System -> R.string.settings_theme_system
        ThemeMode.Light -> R.string.settings_theme_light
        ThemeMode.Dark -> R.string.settings_theme_dark
    }

private val ThemeMode.icon: ImageVector
    get() = when (this) {
        ThemeMode.System -> AppIcons.BrightnessAuto
        ThemeMode.Light -> AppIcons.LightMode
        ThemeMode.Dark -> AppIcons.DarkMode
    }

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemeModeItemPreview() {
    ShoppingAppTheme {
        Surface {
            Column {
                ThemeMode.entries.forEach { ThemeModeItem(selected = it, onSelect = {}) }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NetworkDelayItemPreview() {
    ShoppingAppTheme {
        Surface {
            Column {
                NetworkDelay.entries.forEach { NetworkDelayItem(selected = it, onSelect = {}) }
            }
        }
    }
}
