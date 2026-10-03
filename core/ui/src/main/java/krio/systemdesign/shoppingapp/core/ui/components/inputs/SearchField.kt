package krio.systemdesign.shoppingapp.core.ui.components.inputs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CloseIconButton

// Поле поиска: лупа слева, а когда в поле что-то введено, справа крестик, который его очищает.
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                CloseIconButton(onClick = onClear)
            }
        },
        singleLine = true,
    )
}
