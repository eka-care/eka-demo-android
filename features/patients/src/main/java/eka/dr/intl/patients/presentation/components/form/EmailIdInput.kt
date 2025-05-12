package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun EmailIdInput(emailId: String, onEmailIdChange: (String) -> Unit) {
    OutlinedInputField(
        label = "Email ID",
        value = emailId,
        onValueChange = onEmailIdChange,
        icon = R.drawable.ic_envelope_regular
    )
}
