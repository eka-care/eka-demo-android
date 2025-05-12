package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun NameOfInformantInput(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedInputField(
        label = "Name of informant",
        value = name,
        onValueChange = onNameChange,
        icon = R.drawable.ic_circle_info_regular
    )
}
