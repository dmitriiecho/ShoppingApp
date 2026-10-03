package krio.systemdesign.shoppingapp.core.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Кнопка действия, которое выполняется не сразу, например оформление заказа или проверка промокода:
// пока оно идёт, вместо текста крутится индикатор.
@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // Button сам не анимирует смену фона при enabled, поэтому фон анимируется здесь
    // и передаётся одинаковым для обоих состояний.
    val defaultColors = ButtonDefaults.buttonColors()
    val containerColor by animateColorAsState(
        targetValue = if (enabled) defaultColors.containerColor else defaultColors.disabledContainerColor,
        animationSpec = tween(CONTAINER_COLOR_DURATION_MS),
        label = "loadingButtonContainerColor",
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
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
            Text(text)
        }
    }
}

private const val CONTAINER_COLOR_DURATION_MS = 400
