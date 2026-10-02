package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Плашка с иконкой для пометок на карточках товаров и у промокода.
// Фон — лёгкий оттенок цвета акцента, текст и иконка — сам цвет акцента.
@Composable
fun Notice(
    icon: ImageVector,
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = NOTICE_BACKGROUND_ALPHA),
        contentColor = accentColor,
    ) {
        Row(
            // У кнопки свои отступы и высота, поэтому с ней плашке свои почти не нужны.
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

// Плашка с текстовой кнопкой того же цвета справа.
@Composable
fun NoticeWithAction(
    icon: ImageVector,
    title: String,
    accentColor: Color,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Notice(
        icon = icon,
        title = title,
        accentColor = accentColor,
        modifier = modifier,
        subtitle = subtitle,
        action = {
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor),
            ) {
                Text(actionText)
            }
        },
    )
}

private const val NOTICE_BACKGROUND_ALPHA = 0.14f
