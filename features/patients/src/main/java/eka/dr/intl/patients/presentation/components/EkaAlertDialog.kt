package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import eka.care.documents.ui.touchBodyRegular
import eka.care.documents.ui.touchLabelRegular
import eka.care.documents.ui.touchTitle3Bold

@Composable
fun EkaAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    dialogTitle: String = "Title",
    dialogText: String = "Lorem ipsum dolor sit amet. " +
            "Id sint incidunt qui dolorem necessitatibus " +
            "non voluptatem tempore ut expedita facilis quo " +
            "reiciendis harum. Aut labore asperiores vel " +
            "ducimus alias sit saepe enim.",
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Dismiss",
    painterResourceId: Int? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        ),
        confirmButton = {
            FilledTonalButton(
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp),
                onClick = onConfirmButtonClick
            ) {
                Text(text = confirmButtonText, style = touchLabelRegular)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text(text = dismissButtonText, style = touchLabelRegular)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(painterResourceId != null) {
                    Icon(
                        painter = painterResource(id = painterResourceId),
                        contentDescription = "Location On",
                        modifier = Modifier
                            .padding(end = ButtonDefaults.IconSpacing)
                            .size(34.dp)
                    )
                    Text(
                        text = dialogTitle,
                        style = touchTitle3Bold
                    )
                } else {
                    Text(
                        text = dialogTitle,
                        style = touchTitle3Bold
                    )
                }
            }
        },
        text = {
            Text(
                text = dialogText,
                style = touchBodyRegular
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview
@Composable
fun EkaAlertDialogPreivew() {
    EkaAlertDialog(
        onDismissRequest = { /* Todo */ },
        onConfirmButtonClick = { /* Todo */ },
        onDismissButtonClick = { /* Todo */ },
    )
}