package eka.care.doctor.patients.presentation.ui.components.form.generic


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper

@Composable
fun OutlinedInputField(
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: Int,
    placeholder: String = label,
    supportingText: String = " ",
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        },
        headlineContent = {
            TextFieldWrapper(
                placeholder = placeholder,
                value = value,
                onChange = {
                    onValueChange(it)
                },
                keyboardOptions = keyboardOptions,
                supportingText = supportingText,
                label = label,
                modifier = Modifier.fillMaxWidth()
            )
        },
    )
}