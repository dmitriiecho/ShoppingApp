package krio.systemdesign.shoppingapp.core.designsystem.components.inputs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing

@Composable
fun AppTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    TextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        label = { Text(label) },
        lineLimits = if (singleLine) {
            TextFieldLineLimits.SingleLine
        } else {
            TextFieldLineLimits.MultiLine(maxHeightInLines = 3)
        },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppTextFieldPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = ShoppingAppTheme.colors.cardContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.CardPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppTextField(
                            state = rememberTextFieldState(),
                            label = "Label",
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppTextField(
                            state = rememberTextFieldState("Text"),
                            label = "Label",
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppTextField(
                            state = rememberTextFieldState("Text"),
                            label = "Label",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                        )
                        AppTextField(
                            state = rememberTextFieldState(
                                "A longer text that doesn't fit on one line and goes on to the next one",
                            ),
                            label = "Multiline field",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                        )
                    }
                }
            }
        }
    }
}
