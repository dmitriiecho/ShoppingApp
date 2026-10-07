package krio.systemdesign.shoppingapp.core.designsystem.components.notices

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@Composable
fun Notice(
    icon: ImageVector,
    title: String,
    style: NoticeStyle,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val accentColor = style.accentColor()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = accentColor.copy(alpha = NOTICE_BACKGROUND_ALPHA),
        contentColor = accentColor,
    ) {
        Row(
            // The button brings its own padding and height, so the notice needs very little.
            modifier = if (action != null) {
                Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp)
            } else {
                Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            action?.invoke()
        }
    }
}

@Composable
fun NoticeWithAction(
    icon: ImageVector,
    title: String,
    style: NoticeStyle,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Notice(
        icon = icon,
        title = title,
        style = style,
        modifier = modifier,
        subtitle = subtitle,
        action = {
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(contentColor = style.accentColor()),
            ) {
                Text(actionText)
            }
        },
    )
}

enum class NoticeStyle {
    Error,
    Success,
    Neutral,
}

@Composable
@ReadOnlyComposable
private fun NoticeStyle.accentColor(): Color = when (this) {
    NoticeStyle.Error -> MaterialTheme.colorScheme.error
    NoticeStyle.Success -> ShoppingAppTheme.colors.success
    NoticeStyle.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
}

private const val NOTICE_BACKGROUND_ALPHA = 0.14f

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoticeProductIssuesPreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Notice(
                    icon = AppIcons.Inventory2,
                    title = "Out of stock",
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.Sell,
                    title = "Price changed: now $149.99",
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = "Only 1 available to order now",
                    style = NoticeStyle.Error,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoticePromoCodePreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = "Promo code SALE10 is no longer valid",
                    style = NoticeStyle.Error,
                    actionText = "Remove",
                    onAction = {},
                    subtitle = "Remove it to place your order",
                )
                NoticeWithAction(
                    icon = AppIcons.CheckCircle,
                    title = "Promo code SALE10 · −10%",
                    style = NoticeStyle.Success,
                    actionText = "Remove",
                    onAction = {},
                )
                Notice(
                    icon = AppIcons.CheckCircle,
                    title = "Promo code SALE25 · −25%",
                    style = NoticeStyle.Success,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoticeCheckoutPreview() {
    ShoppingAppTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Notice(
                icon = AppIcons.Info,
                title = "This is a demo app: the order isn't sent anywhere. " +
                    "Placing it empties the cart and its promo code",
                style = NoticeStyle.Neutral,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}
