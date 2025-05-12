package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.icons.R
import eka.dr.intl.patients.presentation.components.EkaAlertDialog
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper
import eka.dr.intl.ui.molecule.ButtonWrapper
import eka.dr.intl.ui.molecule.ButtonWrapperType

@Composable
fun UhidInput(value: String, onValueChange: (String) -> Unit, onGenerateClick: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        val onDismissRequest = { showDialog.value = false }
        val onConfirmButtonClick = {
            onGenerateClick()
            showDialog.value = false
        }

        EkaAlertDialog(
            painterResourceId = R.drawable.ic_circle_info_regular,
            dialogTitle = "Generate UHID",
            dialogText = "Are you sure you want to generate new UHID?",
            onDismissRequest = onDismissRequest,
            onConfirmButtonClick = onConfirmButtonClick,
            onDismissButtonClick = onDismissRequest,
        )

    }
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_id_card_regular,
                contentDescription = "Uhid",
                modifier = Modifier.size(20.dp)
            )
        },
        headlineContent = {
            TextFieldWrapper(
                value = value,
                onChange = {
                    onValueChange(it)
                },
                label = "Uhid"
            )
        },
        trailingContent = {
            ButtonWrapper(
                type = ButtonWrapperType.TEXT,
                text = "Generate",
                onClick = {
                    if (value.isNotEmpty()) {
                        showDialog.value = true
                    } else {
                        onGenerateClick()
                    }
                }
            )
        }
    )
}