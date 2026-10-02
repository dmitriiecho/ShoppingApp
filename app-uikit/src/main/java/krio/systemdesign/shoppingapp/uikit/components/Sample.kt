package krio.systemdesign.shoppingapp.uikit.components

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

// Примеры раздела: группы по компонентам (sampleGroup), у каждой группы заголовок с именем компонента.
// innerPadding — отступы от верхней панели и системных панелей, которые даёт Scaffold экрана.
@Composable
fun SampleList(
    innerPadding: PaddingValues,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // Отступом, а не contentPadding: закреплённый заголовок группы встаёт под верхней панелью, а не за ней.
            .padding(innerPadding)
            // Отступ от панели навигации уже есть в innerPadding. Без этого компоненты, которые сами отступают
            // от неё (TotalBottomBar, NavigationBar), добавили бы его ещё раз.
            .consumeWindowInsets(innerPadding)
            .imePadding(),
        contentPadding = PaddingValues(bottom = 24.dp),
        content = content,
    )
}

// Группа примеров одного компонента. Заголовок закреплён вверху, пока на экране варианты этой группы,
// поэтому всегда видно, какой компонент сейчас перед глазами. content — варианты (SampleVariant).
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

// Имя компонента, как в коде, на подложке во всю ширину: подложка закрывает примеры, которые уезжают под неё.
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

// Вариант компонента: подпись словами и сам компонент в рамке. Внутри рамки фон страницы, как в приложении,
// поэтому карточки и плашки выглядят так же, как на экранах. Без подписи — у компонента один вариант.
// contentPadding — отступ внутри рамки; 0 для компонентов со своими отступами, например AppListItem.
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

// Переключатель с подписью: включает в примере загрузку, анимацию или видимость.
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
