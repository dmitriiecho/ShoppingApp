package krio.systemdesign.shoppingapp.core.ui.components.cards

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
import krio.systemdesign.shoppingapp.core.ui.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

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
                    icon = AppIcons.Contrast,
                    title = "Theme",
                    description = "System",
                    trailing = {
                        SingleChoiceButtons(
                            options = listOf(AppIcons.SystemTheme, AppIcons.LightTheme, AppIcons.DarkTheme),
                            selected = AppIcons.SystemTheme,
                            onSelect = {},
                            modifier = Modifier.width(168.dp),
                        ) { icon ->
                            Icon(icon, contentDescription = null)
                        }
                    },
                )
                AppListItem(
                    icon = AppIcons.HourglassEmpty,
                    title = "Request delay",
                    description = "No delay",
                    trailing = {
                        SingleChoiceButtons(
                            options = listOf(0, 2, 4),
                            selected = 0,
                            onSelect = {},
                            modifier = Modifier.width(168.dp),
                        ) { seconds ->
                            Text(seconds.toString())
                        }
                    },
                )
                AppListItem(
                    icon = AppIcons.Link,
                    title = "Deep links",
                    description = "Opens a page with the links in your browser",
                    onClick = {},
                    trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
                )
                AppListItem(
                    icon = AppIcons.OutOfStock,
                    title = "Add an out-of-stock item to the cart",
                    description = "To check how the cart shows an unavailable item",
                    onClick = {},
                )
            }
        }
    }
}
