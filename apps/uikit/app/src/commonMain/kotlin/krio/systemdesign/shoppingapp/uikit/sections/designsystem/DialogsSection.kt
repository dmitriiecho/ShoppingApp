package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import krio.systemdesign.shoppingapp.core.designsystem.components.dialogs.ConfirmationDialog
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_apply_confirm
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_apply_title
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_delete_confirm
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_delete_message
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_delete_title
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_notice_subtitle
import krio.systemdesign.shoppingapp.uikit.resources.uikit_show_dialog
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_destructive
import krio.systemdesign.shoppingapp.uikit.resources.uikit_variant_regular
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DialogsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("ConfirmationDialog") {
            SampleVariant(caption = stringResource(Res.string.uikit_variant_destructive)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(Res.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(Res.string.uikit_sample_delete_title),
                        text = stringResource(Res.string.uikit_sample_delete_message),
                        confirmText = stringResource(Res.string.uikit_sample_delete_confirm),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                        isDestructive = true,
                    )
                }
            }
            SampleVariant(caption = stringResource(Res.string.uikit_variant_regular)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(Res.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(Res.string.uikit_sample_apply_title),
                        text = stringResource(Res.string.uikit_sample_notice_subtitle),
                        confirmText = stringResource(Res.string.uikit_sample_apply_confirm),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                    )
                }
            }
        }
    }
}
