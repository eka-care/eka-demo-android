package eka.dr.intl.patients.presentation.components.form


import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputDropdown
import eka.dr.intl.icons.R

@Composable
fun PatientOccupationInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedInputDropdown(
        label = "Patient Occupation",
        options = listOf(
            "Self employed", "Employed", "Student", "Housewife", "Retired", "Unemployed", "Other"
        ),
        icon = R.drawable.ic_briefcase_regular,
        selectedOption = value,
        onChangeOption = onValueChange
    )
}

