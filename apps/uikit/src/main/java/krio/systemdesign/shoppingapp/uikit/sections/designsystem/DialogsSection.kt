package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.components.dialogs.ConfirmationDialog
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun DialogsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("ConfirmationDialog") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_destructive)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(R.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(R.string.uikit_sample_delete_title),
                        text = stringResource(R.string.uikit_sample_delete_message),
                        confirmText = stringResource(R.string.uikit_sample_delete_confirm),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                        isDestructive = true,
                    )
                }
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_regular)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(R.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(R.string.uikit_sample_apply_title),
                        text = stringResource(R.string.uikit_sample_notice_subtitle),
                        confirmText = stringResource(R.string.uikit_sample_apply_confirm),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                    )
                }
            }
        }
    }
}
