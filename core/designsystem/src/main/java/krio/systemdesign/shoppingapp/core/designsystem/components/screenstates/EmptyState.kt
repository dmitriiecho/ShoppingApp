package krio.systemdesign.shoppingapp.core.designsystem.components.screenstates

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@Composable
fun EmptyState(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        if (title != null) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
        }
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyStatePreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EmptyState(
                    icon = AppIcons.ShoppingCart,
                    message = "Add products from the catalog",
                    modifier = Modifier.fillMaxWidth(),
                    title = "Your cart is empty",
                )
                EmptyState(
                    icon = AppIcons.Storefront,
                    message = "The catalog is empty",
                    modifier = Modifier.fillMaxWidth(),
                )
                EmptyState(
                    icon = AppIcons.SearchOff,
                    message = "Nothing found for “drone”",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
