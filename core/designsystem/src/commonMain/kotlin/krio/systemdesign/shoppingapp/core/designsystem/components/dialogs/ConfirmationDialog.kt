package krio.systemdesign.shoppingapp.core.designsystem.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.designsystem.resources.Res
import krio.systemdesign.shoppingapp.core.designsystem.resources.designsystem_cancel
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.textButtonColors()
                },
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.designsystem_cancel))
            }
        },
        title = { Text(title) },
        text = { Text(text) },
    )
}

// Here mainly for the screenshot test, which draws the dialog on the whole screen. Android Studio draws only the
// main window, so this preview is blank there; Run Preview shows it on a device.
// The destructive kind, the one the app uses; the regular one differs only in the confirm button's color.
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun ConfirmationDialogPreview() {
    ShoppingAppTheme {
        ConfirmationDialog(
            title = "Delete this?",
            text = "This can't be undone.",
            confirmText = "Delete",
            onConfirm = {},
            onDismiss = {},
            isDestructive = true,
        )
    }
}
