package krio.systemdesign.shoppingapp.core.composeutils.text

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

// UiText for the features that are still Android modules, with their strings in R.string: Compose Multiplatform
// resources work only in KMP modules. Goes away with the last of them.
sealed interface AndroidUiText {

    data class Dynamic(val value: String) : AndroidUiText

    data class Resource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : AndroidUiText
}

fun AndroidUiText.asString(resources: Resources): String = when (this) {
    is AndroidUiText.Dynamic -> value
    is AndroidUiText.Resource -> resources.getString(id, *args.toTypedArray())
}

@Composable
@ReadOnlyComposable
fun AndroidUiText.asString(): String = when (this) {
    is AndroidUiText.Dynamic -> value
    is AndroidUiText.Resource -> stringResource(id, *args.toTypedArray())
}
