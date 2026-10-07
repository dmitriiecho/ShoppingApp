package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppCard
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.OpenInNew
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun CardsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("AppCard") {
            SampleVariant {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    CardText()
                }
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_clickable)) {
                AppCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
                    CardText()
                }
            }
        }
        sampleGroup("SectionCard") {
            SampleVariant {
                SectionCard(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_section_title),
                ) {
                    Text(stringResource(R.string.uikit_sample_card_content))
                }
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_with_subtitle)) {
                SectionCard(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_section_title),
                    subtitle = stringResource(R.string.uikit_sample_notice_subtitle),
                ) {
                    Text(stringResource(R.string.uikit_sample_card_content))
                }
            }
        }
        // AppListItem has its own padding, like list rows on screen, so the frame adds none.
        sampleGroup("AppListItem") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_trailing_icon), contentPadding = 0.dp) {
                AppListItem(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_list_item),
                    description = stringResource(R.string.uikit_sample_list_item_description),
                    onClick = {},
                    trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_trailing_choice), contentPadding = 0.dp) {
                var selected by rememberSaveable { mutableIntStateOf(SAMPLE_OPTIONS.first()) }
                AppListItem(
                    icon = AppIcons.Info,
                    title = stringResource(R.string.uikit_sample_list_item),
                    description = stringResource(R.string.uikit_sample_selected, selected),
                    trailing = {
                        SingleChoiceButtons(
                            options = SAMPLE_OPTIONS,
                            selected = selected,
                            onSelect = { selected = it },
                            modifier = Modifier.width(168.dp),
                        ) { option ->
                            Text(option.toString())
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CardText() {
    Column(modifier = Modifier.padding(Spacing.CardPadding)) {
        Text(
            text = stringResource(R.string.uikit_sample_card_content),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private val SAMPLE_OPTIONS = listOf(1, 2, 3)
