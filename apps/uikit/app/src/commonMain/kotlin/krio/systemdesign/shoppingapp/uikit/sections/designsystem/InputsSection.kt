package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppCard
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.AppOutlinedTextField
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.AppTextField
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.SearchField
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_field_error
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_field_label
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_multiline_field
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_search
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_disabled
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_inside_card
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_on_background
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_with_error
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun InputsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        // AppTextField is meant for a card, so the sample puts it in one.
        sampleGroup("AppTextField") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_inside_card)) {
                val first = rememberTextFieldState()
                val second = rememberTextFieldState()
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(Spacing.CardPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AppTextField(
                            state = first,
                            label = stringResource(Res.string.uikit_sample_field_label),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AppTextField(
                            state = second,
                            label = stringResource(Res.string.uikit_sample_multiline_field),
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_disabled)) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    AppTextField(
                        state = rememberTextFieldState(),
                        label = stringResource(Res.string.uikit_sample_field_label),
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.CardPadding),
                    )
                }
            }
        }
        sampleGroup("AppOutlinedTextField") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_on_background)) {
                AppOutlinedTextField(
                    state = rememberTextFieldState(),
                    label = stringResource(Res.string.uikit_sample_field_label),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_with_error)) {
                AppOutlinedTextField(
                    state = rememberTextFieldState(SAMPLE_TEXT),
                    label = stringResource(Res.string.uikit_sample_field_label),
                    error = stringResource(Res.string.uikit_sample_field_error),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_disabled)) {
                AppOutlinedTextField(
                    state = rememberTextFieldState(SAMPLE_TEXT),
                    label = stringResource(Res.string.uikit_sample_field_label),
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        sampleGroup("SearchField") {
            SampleVariant {
                SearchField(
                    state = rememberTextFieldState(),
                    placeholder = stringResource(Res.string.uikit_sample_search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private const val SAMPLE_TEXT = "Text"
