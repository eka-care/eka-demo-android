package eka.dr.intl.ui.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eka.dr.intl.icons.R
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchLabelRegular

@Composable
fun TextFieldWrapper(
    modifier: Modifier = Modifier,
    value: String = "",
    placeholder: String = "",
    label: String = "",
    onChange: (String) -> Unit = {},
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    supportingText: String = " ",
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors? = null,
    required: Boolean = false
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        onValueChange = onChange,
        keyboardOptions = keyboardOptions,
        colors = (colors ?: OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = DarwinTouchNeutral1000,
            disabledContainerColor = DarwinTouchNeutral0,
            disabledTextColor = DarwinTouchNeutral1000.copy(alpha = 0.38f),
            focusedTextColor = DarwinTouchNeutral1000,
            errorTextColor = DarwinTouchNeutral1000,
            errorSupportingTextColor = DarwinTouchRed,
            focusedSupportingTextColor = DarwinTouchNeutral800,
            disabledSupportingTextColor = DarwinTouchNeutral800.copy(alpha = 0.38f),
            unfocusedSupportingTextColor = DarwinTouchNeutral800,
            errorLeadingIconColor = DarwinTouchNeutral800,
            errorPlaceholderColor = DarwinTouchNeutral600,
            focusedContainerColor = DarwinTouchNeutral0,
            errorCursorColor = DarwinTouchRed,
            errorPrefixColor = DarwinTouchNeutral800,
            errorSuffixColor = DarwinTouchNeutral800,
            disabledPrefixColor = DarwinTouchNeutral800.copy(alpha = 0.38f),
            disabledSuffixColor = DarwinTouchNeutral800.copy(alpha = 0.38f),
            errorContainerColor = DarwinTouchNeutral0,
            unfocusedLabelColor = DarwinTouchNeutral800,
            focusedLabelColor = DarwinTouchPrimary,
            unfocusedPrefixColor = DarwinTouchNeutral800,
            unfocusedSuffixColor = DarwinTouchNeutral800,
            disabledLabelColor = DarwinTouchNeutral1000.copy(alpha = 0.38f),
            focusedPrefixColor = DarwinTouchNeutral800,
            focusedSuffixColor = DarwinTouchNeutral800,
            cursorColor = DarwinTouchPrimary,
            errorLabelColor = DarwinTouchRed,
            errorTrailingIconColor = DarwinTouchRed,
            focusedLeadingIconColor = DarwinTouchNeutral800,
            disabledLeadingIconColor = DarwinTouchNeutral800.copy(alpha = 0.38f),
            unfocusedTrailingIconColor = DarwinTouchNeutral800,
            disabledPlaceholderColor = DarwinTouchNeutral600.copy(alpha = 0.38f),
            focusedTrailingIconColor = DarwinTouchNeutral800,
            unfocusedContainerColor = DarwinTouchNeutral0,
            focusedPlaceholderColor = DarwinTouchNeutral600,
            disabledTrailingIconColor = DarwinTouchNeutral800.copy(alpha = 0.38f),
            unfocusedLeadingIconColor = DarwinTouchNeutral800,
            unfocusedPlaceholderColor = DarwinTouchNeutral600,
            disabledBorderColor = DarwinTouchNeutral800.copy(0.12f),
            errorBorderColor = DarwinTouchRed,
            unfocusedBorderColor = DarwinTouchNeutral200,
            focusedBorderColor = DarwinTouchPrimary
        )),
        value = value,
        enabled = enabled,
        isError = isError,
        supportingText = if (supportingText.isNotEmpty()) ({
            Text(
                text = supportingText,
                style = touchLabelRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }) else null,
        trailingIcon = if (trailingIcon != null) ({
            IconWrapper(
                icon = trailingIcon,
                contentDescription = "Trailing Icon",
                modifier = Modifier.size(16.dp),
            )
        }) else null,
        label = if (label.isNotEmpty()) ({
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    maxLines = 1,
                )
                if (required) {
                    Text(text = "*", color = DarwinTouchRed)
                }
            }
        }) else null,
        leadingIcon = if (leadingIcon != null) ({
            IconWrapper(
                icon = leadingIcon,
                contentDescription = "Leading Icon",
                modifier = Modifier.size(16.dp),
            )
        }) else null,
        placeholder = if (placeholder.isNotEmpty()) ({
            Text(
                text = placeholder,
                style = touchBodyRegular,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }) else null
    )
}

@Preview(showBackground = true, widthDp = 720)
@Composable
fun TextFieldWrapperPreview() {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextFieldWrapper(
                value = "",
                label = "Label",
                placeholder = "Placeholder",
                leadingIcon = R.drawable.ic_magnifying_glass_regular,
                trailingIcon = R.drawable.ic_xmark_regular,
            )

            TextFieldWrapper(
                value = "",
                label = "Label",
                placeholder = "Placeholder",
                supportingText = "Supporting Text",
                leadingIcon = R.drawable.ic_magnifying_glass_regular,
                trailingIcon = R.drawable.ic_xmark_regular,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextFieldWrapper(
                value = "",
                enabled = false,
                label = "Label",
                placeholder = "Placeholder",
                supportingText = "Supporting Text",
                leadingIcon = R.drawable.ic_magnifying_glass_regular,
                trailingIcon = R.drawable.ic_xmark_regular,
            )

            TextFieldWrapper(
                value = "",
                isError = true,
                label = "Label",
                placeholder = "Placeholder",
                supportingText = "Supporting Text",
                leadingIcon = R.drawable.ic_magnifying_glass_regular,
                trailingIcon = R.drawable.ic_xmark_regular,
            )
        }
    }
}