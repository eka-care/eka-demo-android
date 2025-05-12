package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun PatientAddressInput(address: String, onAddressChange: (String) -> Unit) {
    OutlinedInputField(
        icon = R.drawable.ic_circle_info_regular,
        label = "Address",
        value = address,
        onValueChange = onAddressChange
    )
}
