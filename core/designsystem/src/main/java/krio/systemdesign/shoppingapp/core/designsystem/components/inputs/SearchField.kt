package krio.systemdesign.shoppingapp.core.designsystem.components.inputs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.ClearIconButton
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Search
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

// The clear button empties the state itself, so the caller only watches the text.
@Composable
fun SearchField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(AppIcons.Search, contentDescription = null)
        },
        trailingIcon = {
            if (state.text.isNotEmpty()) {
                ClearIconButton(onClick = { state.clearText() })
            }
        },
        lineLimits = TextFieldLineLimits.SingleLine,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchFieldPreview() {
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("", "Text").forEach { query ->
                    SearchField(
                        state = rememberTextFieldState(query),
                        placeholder = "Search",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
