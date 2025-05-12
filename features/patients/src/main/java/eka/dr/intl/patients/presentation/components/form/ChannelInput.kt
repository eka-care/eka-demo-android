package eka.dr.intl.patients.presentation.components.form

import androidx.compose.runtime.Composable
import eka.care.doctor.patients.presentation.ui.components.form.generic.OutlinedInputField
import eka.dr.intl.icons.R

@Composable
fun ChannelInput(channel: String, onChannelChange: (String) -> Unit) {
    OutlinedInputField(
        label = "Channel",
        value = channel,
        onValueChange = onChannelChange,
        icon = R.drawable.ic_circle_info_regular
    )
}
