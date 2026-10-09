package krio.systemdesign.shoppingapp.shared.ui.order

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.LoadingButton
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.shared.ui.R
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

@Composable
fun TotalBottomBar(
    total: String,
    actionText: String,
    enabled: Boolean,
    isLoading: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    // Shown above the "Total" row, e.g. extra buttons.
    header: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                // In landscape the camera cutout can be at the side, under the total.
                .windowInsetsPadding(
                    WindowInsets.navigationBars
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                )
                .padding(horizontal = Spacing.ScreenPadding, vertical = 12.dp)
                .contentWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            header?.invoke(this)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.shared_ui_total),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = total,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Spacer(Modifier.width(16.dp))
                LoadingButton(
                    text = actionText,
                    isLoading = isLoading,
                    onClick = onAction,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TotalBottomBarPreview() {
    val total = formatPrice(25_994)
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TotalBottomBar(
                    total = total,
                    actionText = "Checkout",
                    enabled = true,
                    isLoading = false,
                    onAction = {},
                )
                TotalBottomBar(
                    total = total,
                    actionText = "Checkout",
                    enabled = false,
                    isLoading = true,
                    onAction = {},
                )
                TotalBottomBar(
                    total = total,
                    actionText = "Checkout",
                    enabled = false,
                    isLoading = false,
                    onAction = {},
                    header = {
                        NoticeWithAction(
                            icon = AppIcons.Sell,
                            title = "Price changed for 1 item",
                            style = NoticeStyle.Error,
                            actionText = "Accept",
                            onAction = {},
                        )
                    },
                )
                listOf(false, true).forEach { enabled ->
                    TotalBottomBar(
                        total = total,
                        actionText = "Place order",
                        enabled = enabled,
                        isLoading = false,
                        onAction = {},
                        header = {
                            Notice(
                                icon = AppIcons.Info,
                                title = "This is a demo app: the order isn't sent anywhere. " +
                                    "Placing it empties the cart and its promo code",
                                style = NoticeStyle.Neutral,
                            )
                        },
                    )
                }
            }
        }
    }
}
