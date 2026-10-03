package krio.systemdesign.shoppingapp.core.ui.components.buttons

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Переключатель, в котором выбран ровно один вариант: тема в настройках, способ оплаты при оформлении.
// Ширину задаёт экран, кнопки делят её поровну. label — содержимое кнопки: иконка, текст или оба.
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
    // Выбранный вариант того же цвета, что и выбранная вкладка в нижней панели (AppNavigationBarItem).
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
                // Без галочки: выбранную кнопку видно по цвету, а галочка не помещается рядом с иконкой.
                icon = {},
            ) {
                label(option)
            }
        }
    }
}
