package krio.systemdesign.shoppingapp.core.ui.components.bars

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

@Composable
fun RowScope.AppNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge {
                            Text(if (badgeCount > MAX_BADGE_COUNT) "$MAX_BADGE_COUNT+" else badgeCount.toString())
                        }
                    }
                },
            ) {
                Icon(icon, contentDescription = null)
            }
        },
        modifier = modifier,
        label = { Text(label) },
        colors = colors,
    )
}

// Larger numbers don't fit in the badge, so they show as "99+".
private const val MAX_BADGE_COUNT = 99

@Preview(name = "Light", widthDp = 240)
@Preview(name = "Dark", widthDp = 240, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppNavigationBarItemPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf<@Composable RowScope.(selected: Boolean) -> Unit>(
                    { selected ->
                        AppNavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = Icons.Default.Home,
                            label = "Catalog",
                        )
                    },
                    { selected ->
                        AppNavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = Icons.Default.ShoppingCart,
                            label = "Cart",
                        )
                    },
                    { selected ->
                        AppNavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = Icons.Default.ShoppingCart,
                            label = "Cart",
                            badgeCount = 3,
                        )
                    },
                    { selected ->
                        AppNavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = Icons.Default.ShoppingCart,
                            label = "Cart",
                            badgeCount = 120,
                        )
                    },
                    { selected ->
                        AppNavigationBarItem(
                            selected = selected,
                            onClick = {},
                            icon = Icons.Default.Settings,
                            label = "Settings",
                        )
                    },
                ).forEach { item ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(true, false).forEach { selected ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.small,
                                color = NavigationBarDefaults.containerColor,
                            ) {
                                Row {
                                    item(selected)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
