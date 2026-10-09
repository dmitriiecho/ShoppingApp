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
private fun NoticePreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Notice(
                    icon = AppIcons.Error,
                    title = "Something went wrong",
                    style = NoticeStyle.Error,
                )
                Notice(
                    icon = AppIcons.CheckCircle,
                    title = "Done",
                    style = NoticeStyle.Success,
                )
                Notice(
                    icon = AppIcons.Info,
                    title = "For your information",
                    style = NoticeStyle.Neutral,
                    subtitle = "A short explanation",
                )
                Notice(
                    icon = AppIcons.Info,
                    title = "A longer message that doesn't fit on one line and goes on to the next one",
                    style = NoticeStyle.Neutral,
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoticeWithActionPreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NoticeWithAction(
                    icon = AppIcons.Error,
                    title = "Something went wrong",
                    style = NoticeStyle.Error,
                    actionText = "Action",
                    onAction = {},
                    subtitle = "A short explanation",
                )
                NoticeWithAction(
                    icon = AppIcons.CheckCircle,
                    title = "Done",
                    style = NoticeStyle.Success,
                    actionText = "Action",
                    onAction = {},
                )
            }
        }
    }
}
