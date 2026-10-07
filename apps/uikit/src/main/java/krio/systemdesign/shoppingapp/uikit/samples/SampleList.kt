package krio.systemdesign.shoppingapp.uikit.samples

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing

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
            // innerPadding already clears the nav bar: keeps TotalBottomBar, NavigationBar from adding it again.
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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SampleListPreview() {
    ShoppingAppTheme {
        Surface {
            SampleList(innerPadding = PaddingValues()) {
                sampleGroup("FirstComponent") {
                    SampleVariant(caption = "Caption") { Text("Content") }
                    SampleVariant { Text("Content") }
                }
                sampleGroup("SecondComponent") {
                    SampleVariant { Text("Content") }
                }
            }
        }
    }
}
