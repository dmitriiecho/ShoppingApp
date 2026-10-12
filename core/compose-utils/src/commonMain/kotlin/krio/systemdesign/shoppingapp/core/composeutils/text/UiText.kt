package krio.systemdesign.shoppingapp.core.composeutils.text

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

// Screen text a ViewModel can build without access to resources.
sealed interface UiText {

    data class Dynamic(val value: String) : UiText

    data class Resource(
        val resource: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> stringResource(resource, *args.toTypedArray())
}

// Outside Compose the strings are read from a file in a coroutine (ObserveEffectsScope.showSnackbar).
internal suspend fun UiText.getString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> getString(resource, *args.toTypedArray())
}
