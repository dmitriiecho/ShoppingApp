package krio.systemdesign.shoppingapp.uikit.samples

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_animation
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SampleSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// One switch for all placeholders of a section: without animation they look as after a failed load.
internal fun LazyListScope.sampleAnimationSwitch(
    isAnimating: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    item {
        SampleSwitch(
            label = stringResource(Res.string.uikit_animation),
            checked = isAnimating,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(horizontal = Spacing.ScreenPadding, vertical = 8.dp),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun SampleSwitchPreview() {
    ShoppingAppTheme {
        Surface {
            SampleSwitch(label = "Label", checked = true, onCheckedChange = {}, modifier = Modifier.padding(16.dp))
        }
    }
}
