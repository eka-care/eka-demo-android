package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun GuardianNameInput(name: String, onNameChange: (String) -> Unit) {
    OutlinedInputField(
        label = "Guardian Name",
        value = name,
        onValueChange = onNameChange,
        icon = R.drawable.ic_people_pants_regular
    )
}
