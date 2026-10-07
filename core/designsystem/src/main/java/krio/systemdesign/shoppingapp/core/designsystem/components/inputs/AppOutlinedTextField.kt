package krio.systemdesign.shoppingapp.core.designsystem.components.inputs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

// A single-line field on the screen background; inside a card use AppTextField.
// An error shows below the field and turns the outline red.
@Composable
fun AppOutlinedTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppOutlinedTextFieldPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppOutlinedTextField(
                    state = rememberTextFieldState(),
                    label = "Promo code",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppOutlinedTextField(
                    state = rememberTextFieldState("SALE10"),
                    label = "Promo code",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppOutlinedTextField(
                    state = rememberTextFieldState("SALE99"),
                    label = "Promo code",
                    error = "Promo code not found",
                    modifier = Modifier.fillMaxWidth(),
                )
                AppOutlinedTextField(
                    state = rememberTextFieldState("SALE10"),
                    label = "Promo code",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
