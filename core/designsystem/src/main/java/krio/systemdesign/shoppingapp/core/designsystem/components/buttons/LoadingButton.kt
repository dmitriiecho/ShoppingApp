package krio.systemdesign.shoppingapp.core.designsystem.components.buttons

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // Button doesn't animate its background when enabled changes, so we animate it here for both states.
    val defaultColors = ButtonDefaults.buttonColors()
    val containerColor by animateColorAsState(
        targetValue = if (enabled) defaultColors.containerColor else defaultColors.disabledContainerColor,
        animationSpec = tween(CONTAINER_COLOR_DURATION_MS),
        label = "loadingButtonContainerColor",
    )
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingButtonPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LoadingButton(
                    text = "Apply",
                    isLoading = false,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                LoadingButton(
                    text = "Apply",
                    isLoading = false,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                )
                LoadingButton(
                    text = "Apply",
                    isLoading = true,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                )
            }
        }
    }
}
