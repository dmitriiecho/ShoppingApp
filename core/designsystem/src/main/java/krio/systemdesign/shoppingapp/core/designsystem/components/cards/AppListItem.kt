package krio.systemdesign.shoppingapp.core.designsystem.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.OpenInNew
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@Composable
fun AppListItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(title) },
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        supportingContent = { Text(description) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = trailing,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppListItemPreview() {
    ShoppingAppTheme {
        Surface {
            Column {
                AppListItem(
                    icon = AppIcons.Info,
                    title = "List item",
                    description = "Selected: 1",
                    trailing = {
                        SingleChoiceButtons(
                            options = listOf(1, 2, 3),
                            selected = 1,
                            onSelect = {},
                            modifier = Modifier.width(168.dp),
                        ) { option ->
                            Text(option.toString())
                        }
                    },
                )
                AppListItem(
                    icon = AppIcons.Info,
                    title = "List item",
                    description = "Item description",
                    onClick = {},
                    trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
                )
                AppListItem(
                    icon = AppIcons.Info,
                    title = "List item",
                    description = "A longer description that doesn't fit on one line and goes on to the next one",
                    onClick = {},
                )
            }
        }
    }
}
