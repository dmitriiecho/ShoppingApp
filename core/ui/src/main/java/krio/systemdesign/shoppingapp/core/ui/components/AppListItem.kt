package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Строка списка, например пункт настроек: значок, заголовок и пояснение под ним, справа trailing —
// значок или переключатель. onClick = null — сама строка не нажимается, например когда действие в переключателе.
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
        supportingContent = { Text(description) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = trailing,
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
    )
}
