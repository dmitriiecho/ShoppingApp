package krio.systemdesign.shoppingapp.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R

@Composable
fun TotalBottomBar(
    total: String,
    actionText: String,
    enabled: Boolean,
    isLoading: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    // Что показать над строкой «Итого» в той же панели, например дополнительные кнопки.
    header: (@Composable ColumnScope.() -> Unit)? = null,
) {
    // Цвет как у панели вкладок (NavigationBar): внизу экрана они складываются в один блок.
    // Приподнятая Surface с tonalElevation подмешивала бы акцентный цвет, и панель становилась бы оранжеватой.
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        // Отступ от системной навигации, фон Surface при этом остаётся до края экрана.
        // Внутри табов этот отступ уже учла панель вкладок, и здесь он будет нулевым.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            header?.invoke(this)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.core_ui_total),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = total,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.width(16.dp))
                // Button сам не анимирует смену фона при enabled, поэтому фон анимируется здесь
                // и передаётся одинаковым для обоих состояний.
                val defaultColors = ButtonDefaults.buttonColors()
                val containerColor by animateColorAsState(
                    targetValue = if (enabled) defaultColors.containerColor else defaultColors.disabledContainerColor,
                    animationSpec = tween(CONTAINER_COLOR_DURATION_MS),
                    label = "actionContainerColor",
                )
                Button(
                    onClick = onAction,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        disabledContainerColor = containerColor,
                    ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(actionText)
                    }
                }
            }
        }
    }
}

private const val CONTAINER_COLOR_DURATION_MS = 400
