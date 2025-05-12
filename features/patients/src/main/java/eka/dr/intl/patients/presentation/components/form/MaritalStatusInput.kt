package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputDropdown
import eka.dr.intl.icons.R

@Composable
fun MaritalStatusInput(
    listOfMaritalStatus: List<String>,
    maritalStatus: String,
    onMaritalStatusChange: (String) -> Unit,
) {
    OutlinedInputDropdown(
        label = "Marital Status",
        options = listOfMaritalStatus,
        selectedOption = maritalStatus,
        onChangeOption = onMaritalStatusChange,
        icon = R.drawable.ic_ring_diamond_regular
    )
}