package eka.dr.intl.assistant.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchTitle2Regular

@Composable
fun PermissionAlertDialog(
    title: String,
    description: String,
    confirmButtonLabel: String = "Okay",
    dismissButtonLabel: String = "Cancel",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        containerColor = DarwinTouchNeutral50,
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = touchTitle2Regular,
                color = DarwinTouchNeutral1000
            )
        },
        text = {
            Text(
                description,
                style = touchCalloutRegular,
                color = DarwinTouchNeutral800
            )
        },
        confirmButton = {
            if (!confirmButtonLabel.isNullOrEmpty()) {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text(
                        confirmButtonLabel,
                        color = DarwinTouchPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        dismissButton = {
            if (!dismissButtonLabel.isNullOrEmpty()) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        dismissButtonLabel,
                        color = DarwinTouchPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}