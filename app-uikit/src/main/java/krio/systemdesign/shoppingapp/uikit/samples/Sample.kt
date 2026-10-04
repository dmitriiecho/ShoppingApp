package krio.systemdesign.shoppingapp.uikit.samples

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing

// A section's samples, grouped by component (sampleGroup). innerPadding comes from the screen's Scaffold.
@Composable
fun SampleList(
    innerPadding: PaddingValues,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // padding, not contentPadding: a pinned group header sits below the top bar, not behind it.
            .padding(innerPadding)
            // innerPadding already clears the navigation bar; without this, components that clear it themselves
            // (TotalBottomBar, NavigationBar) would add it again.
            .consumeWindowInsets(innerPadding)
            .imePadding(),
        contentPadding = PaddingValues(bottom = 24.dp),
        content = content,
    )
}

// One component's variants (SampleVariant) under a header that stays pinned while they are on screen.
fun LazyListScope.sampleGroup(
    name: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    stickyHeader {
        SampleGroupHeader(name)
    }
    item {
        Column(
            modifier = Modifier.padding(Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = content,
        )
    }
}

// The component name as in code, on a full-width background that covers samples scrolling under it.
@Composable
private fun SampleGroupHeader(name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = Spacing.ScreenPadding, vertical = 12.dp),
        )
    }
}

// One variant: an optional caption and the component in a frame with the page background, as on the app's screens.
// contentPadding: 0 for components with their own padding, e.g. AppListItem.
@Composable
fun SampleVariant(
    caption: String? = null,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

// A labeled switch for a sample's loading, animation or visibility.
@Composable
fun SampleSwitch(
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
