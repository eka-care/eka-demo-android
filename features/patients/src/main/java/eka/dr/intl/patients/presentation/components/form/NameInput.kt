package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eka.dr.intl.ui.atom.DropdownMenuWithDetails
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R

@Composable
fun NameInput(
    salutation: String,
    onSalutationChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    isSalutationEnabled: Boolean = true
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_user_regular,
                contentDescription = "Mobile",
                modifier = Modifier.size(20.dp)
            )
        },
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSalutationEnabled) {
                    DropdownMenuWithDetails(
                        label = "Salutation",
                        disabled = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = DarwinTouchNeutral200,
                            disabledTextColor = DarwinTouchNeutral1000,
                            disabledTrailingIconColor = DarwinTouchNeutral800,
                            disabledPlaceholderColor = DarwinTouchNeutral800,
                            disabledLabelColor = DarwinTouchNeutral800
                        ),
                        options = listOf(
                            "Mr.",
                            "Ms.",
                            "Mrs.",
                            "Miss.",
                            "Kumar.",
                            "Shri.",
                            "Smt.",
                            "Dr.",
                            "Master.",
                            "Baby.",
                            "Mohd.",
                            "B/O"
                        ),
                        modifier = Modifier.width(120.dp),
                        value = salutation,
                        dropdownMenuItem = { it, onClick ->
                            DropdownMenuItem(onClick = {
                                onSalutationChange(it)
                                onClick()
                            }, text = {
                                Text(text = it)
                            })
                        }
                    )
                }
                TextFieldWrapper(
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    required = true,
                    supportingText = " ",
                    value = name,
                    onChange = {
                        onNameChange(it)
                    },
                    label = "Patient Name",
                    modifier = Modifier.weight(1f),
                    isError = name.isNullOrEmpty(),
                )
            }
        }
    )
}