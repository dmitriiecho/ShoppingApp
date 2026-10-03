package krio.systemdesign.shoppingapp.core.ui.components.inputs

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Поле ввода внутри карточки: залитое, без рамки и со скруглением, как у плашек и карточек.
// Обычное OutlinedTextField с острыми углами и рамкой в скруглённой карточке выглядит чужим.
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        enabled = enabled,
        singleLine = singleLine,
        // Многострочное поле растёт вместе с текстом, но не больше трёх строк, дальше прокручивается.
        maxLines = if (singleLine) 1 else 3,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}
