package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.CloseIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.LoadingButton
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.ScrollToTopButton
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleSwitch
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.rememberSampleLoading
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun ButtonsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("LoadingButton") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_tap_to_load)) {
                var isLoading by rememberSampleLoading()
                LoadingButton(
                    text = stringResource(R.string.uikit_sample_button),
                    isLoading = isLoading,
                    onClick = { isLoading = true },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_disabled)) {
                LoadingButton(
                    text = stringResource(R.string.uikit_sample_button),
                    isLoading = false,
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        sampleGroup("SingleChoiceButtons") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_icon_and_text)) {
                var selected by rememberSaveable { mutableStateOf(SampleOption.Light) }
                SingleChoiceButtons(
                    options = SampleOption.entries,
                    selected = selected,
                    onSelect = { selected = it },
                    modifier = Modifier.fillMaxWidth(),
                ) { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(option.titleRes))
                    }
                }
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_fixed_width)) {
                var selected by rememberSaveable { mutableIntStateOf(SAMPLE_OPTIONS.first()) }
                SingleChoiceButtons(
                    options = SAMPLE_OPTIONS,
                    selected = selected,
                    onSelect = { selected = it },
                    modifier = Modifier.width(168.dp),
                ) { option ->
                    Text(option.toString())
                }
            }
        }
        sampleGroup("CloseIconButton") {
            SampleVariant {
                CloseIconButton(onClick = {})
            }
        }
        sampleGroup("NavigateBackIconButton") {
            SampleVariant {
                NavigateBackIconButton(onClick = {})
            }
        }
        sampleGroup("ScrollToTopButton") {
            SampleVariant {
                var isVisible by rememberSaveable { mutableStateOf(true) }
                SampleSwitch(
                    label = stringResource(R.string.uikit_visible),
                    checked = isVisible,
                    onCheckedChange = { isVisible = it },
                )
                ScrollToTopButton(
                    visible = isVisible,
                    onClick = { isVisible = false },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

private enum class SampleOption(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
) {
    Light(AppIcons.LightMode, R.string.uikit_sample_option_light),
    Dark(AppIcons.DarkMode, R.string.uikit_sample_option_dark),
}

private val SAMPLE_OPTIONS = listOf(1, 2, 3)
