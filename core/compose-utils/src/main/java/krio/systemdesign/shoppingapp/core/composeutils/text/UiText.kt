package krio.systemdesign.shoppingapp.core.composeutils.text

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

// Screen text a ViewModel can build without access to resources.
sealed interface UiText {

    data class Dynamic(val value: String) : UiText

    data class Resource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText
}

fun UiText.asString(resources: Resources): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> resources.getString(id, *args.toTypedArray())
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> stringResource(id, *args.toTypedArray())
}
