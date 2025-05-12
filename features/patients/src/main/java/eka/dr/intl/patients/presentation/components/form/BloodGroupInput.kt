package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputDropdown
import eka.dr.intl.icons.R

@Composable
fun BloodGroupInput(bloodGroup: String, onBloodGroupChange: (String) -> Unit) {
    OutlinedInputDropdown(
        label = "Blood Group",
        icon = R.drawable.ic_droplet_regular,
        options = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
        selectedOption = bloodGroup,
        onChangeOption = { onBloodGroupChange(it) }
    )
}