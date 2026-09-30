package krio.systemdesign.shoppingapp.core.ui.text

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

// Текст для экрана, который ViewModel может собрать без доступа к ресурсам.
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
