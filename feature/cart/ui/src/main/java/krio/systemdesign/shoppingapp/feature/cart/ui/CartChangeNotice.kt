package krio.systemdesign.shoppingapp.feature.cart.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.Notice
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

// A notice above the cart total, one per kind of change the cart validation found, with a button that fixes
// them all when there is one. Every notice is as tall as one with a button, so the stack lines up.
@Composable
fun CartChangeNotice(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    val noticeModifier = modifier.heightIn(min = MinHeight)
    if (actionText != null) {
        NoticeWithAction(
            icon = icon,
            title = title,
            style = NoticeStyle.Error,
            actionText = actionText,
            onAction = onAction,
            modifier = noticeModifier,
        )
    } else {
        Notice(
            icon = icon,
            title = title,
            style = NoticeStyle.Error,
            modifier = noticeModifier,
        )
    }
}

// A 48dp button for an easy tap plus the notice's 4dp padding above and below.
private val MinHeight = 56.dp

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartChangeNoticePreview() {
    ShoppingAppTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CartChangeNotice(
                    icon = AppIcons.Sell,
                    title = "Price changed for 1 item",
                    actionText = "Accept",
                )
                CartChangeNotice(
                    icon = AppIcons.Inventory2,
                    title = "1 item is out of stock",
                    actionText = "Remove",
                )
                CartChangeNotice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = "Not enough stock for 1 item",
                )
            }
        }
    }
}
