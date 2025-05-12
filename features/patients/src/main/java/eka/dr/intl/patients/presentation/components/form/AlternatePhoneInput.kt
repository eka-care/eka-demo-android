package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun AlternatePhoneInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedInputField(
        label = "Alternate Phone",
        value = value,
        onValueChange = onValueChange,
        icon = R.drawable.ic_phone_regular,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
    )
}