package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.components.dialogs.ConfirmationDialog
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup

@Composable
fun DialogsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("ConfirmationDialog") {
            SampleVariant(stringResource(R.string.uikit_variant_destructive)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(R.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(R.string.uikit_sample_clear_cart_title),
                        text = stringResource(R.string.uikit_sample_clear_cart_message),
                        confirmText = stringResource(R.string.uikit_sample_clear_cart_confirm),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                        isDestructive = true,
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_regular)) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                Button(onClick = { isVisible = true }) {
                    Text(stringResource(R.string.uikit_show_dialog))
                }
                if (isVisible) {
                    ConfirmationDialog(
                        title = stringResource(R.string.uikit_sample_accept_prices_title),
                        text = stringResource(R.string.uikit_sample_accept_prices_message),
                        confirmText = stringResource(R.string.uikit_sample_accept),
                        onConfirm = { isVisible = false },
                        onDismiss = { isVisible = false },
                    )
                }
            }
        }
    }
}
