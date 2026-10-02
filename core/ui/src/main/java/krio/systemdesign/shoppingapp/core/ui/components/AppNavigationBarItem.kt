package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Вкладка нижней панели (NavigationBar). badgeCount > 0 — счётчик на значке, например товаров в корзине.
@Composable
fun RowScope.AppNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    // Выбранная вкладка — акцентного цвета на подложке primaryContainer. Со стандартными цветами
    // (secondaryContainer и почти такая же иконка, как у остальных) выбранную вкладку плохо видно.
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

// Большее число в маленький кружок не помещается, вместо него «99+».
private const val MAX_BADGE_COUNT = 99
