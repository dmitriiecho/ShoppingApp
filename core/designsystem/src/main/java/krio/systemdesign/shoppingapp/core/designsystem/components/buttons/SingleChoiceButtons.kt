package krio.systemdesign.shoppingapp.core.designsystem.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.BrightnessAuto
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.CreditCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Payments
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SingleChoiceButtons(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (T) -> Unit,
) {
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                enabled = enabled,
                colors = colors,
                icon = {},
            ) {
                label(option)
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SingleChoiceButtonsPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val themes = listOf(
                    AppIcons.BrightnessAuto to "System",
                    AppIcons.LightMode to "Light",
                    AppIcons.DarkMode to "Dark",
                )
                SingleChoiceButtons(
                    options = themes,
                    selected = themes.first(),
                    onSelect = {},
                    modifier = Modifier.width(168.dp),
                ) { (icon, title) ->
                    Icon(icon, contentDescription = title)
                }
                SingleChoiceButtons(
                    options = listOf(0, 2, 4),
                    selected = 0,
                    onSelect = {},
                    modifier = Modifier.width(168.dp),
                ) { seconds ->
                    Text(seconds.toString())
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ShoppingAppTheme.colors.cardContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val paymentMethods = listOf(
                            AppIcons.CreditCard to "Card",
                            AppIcons.Payments to "Cash",
                        )
                        listOf(true, false).forEach { enabled ->
                            SingleChoiceButtons(
                                options = paymentMethods,
                                selected = paymentMethods.first(),
                                onSelect = {},
                                modifier = Modifier.fillMaxWidth(),
                                enabled = enabled,
                            ) { (icon, title) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(title)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
