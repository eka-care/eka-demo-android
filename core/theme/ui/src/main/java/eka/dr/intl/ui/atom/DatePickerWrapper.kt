package eka.dr.intl.ui.atom

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral400
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ui.molecule.ButtonWrapper
import eka.dr.intl.ui.molecule.ButtonWrapperType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    selectedDate: String,
    label: String = "DOB",
    placeholder: String = "dd/MM/yyyy",
    onDateSelected: (String) -> Unit,
    trailingIcon: Int? = null,
    required: Boolean = false,
    datePickerState: DatePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
) {
    var showModal by remember { mutableStateOf(false) }

    TextFieldWrapper(
        value = selectedDate,
        onChange = { },
        required = required,
        label = label,
        placeholder = placeholder,
        enabled = false,
        supportingText = " ",
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = DarwinTouchNeutral200,
            disabledTextColor = DarwinTouchNeutral1000,
            disabledTrailingIconColor = DarwinTouchNeutral800,
            disabledPlaceholderColor = DarwinTouchNeutral800,
            disabledLabelColor = DarwinTouchNeutral800
        ),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModalInput(
            onDateSelected = { it?.let { onDateSelected(convertMillisToDate(it)) } },
            onDismiss = { showModal = false },
            datePickerState = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
) {

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = DarwinTouchNeutral50,
            titleContentColor = DarwinTouchNeutral1000,
            headlineContentColor = DarwinTouchNeutral1000,
            weekdayContentColor = DarwinTouchNeutral1000,
            subheadContentColor = DarwinTouchNeutral1000,
            navigationContentColor = DarwinTouchNeutral1000,
            yearContentColor = DarwinTouchNeutral800,
            disabledYearContentColor = DarwinTouchNeutral400,
            currentYearContentColor = DarwinTouchPrimary,
            selectedYearContentColor = DarwinTouchNeutral0,
            disabledSelectedYearContentColor = DarwinTouchNeutral0,
            selectedYearContainerColor = DarwinTouchPrimary,
            disabledSelectedYearContainerColor = DarwinTouchNeutral400,
            dayContentColor = DarwinTouchNeutral1000,
            disabledDayContentColor = DarwinTouchNeutral400,
            selectedDayContentColor = DarwinTouchNeutral0,
            disabledSelectedDayContentColor = DarwinTouchNeutral0,
            selectedDayContainerColor = DarwinTouchPrimary,
            disabledSelectedDayContainerColor = DarwinTouchNeutral400,
            todayContentColor = DarwinTouchPrimary,
            todayDateBorderColor = DarwinTouchPrimary,
            dayInSelectionRangeContentColor = DarwinTouchNeutral0,
            dayInSelectionRangeContainerColor = DarwinTouchPrimary,
            dividerColor = DarwinTouchNeutral200,
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            ButtonWrapper(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }, type = ButtonWrapperType.TEXT, text = "OK")
        },
        dismissButton = {
            ButtonWrapper(onClick = onDismiss, type = ButtonWrapperType.TEXT, text = "Cancel")
        }
    ) {
        DatePicker(
            state = datePickerState, colors = DatePickerDefaults.colors(
                containerColor = DarwinTouchNeutral50,
                titleContentColor = DarwinTouchNeutral1000,
                headlineContentColor = DarwinTouchNeutral1000,
                weekdayContentColor = DarwinTouchNeutral1000,
                subheadContentColor = DarwinTouchNeutral1000,
                navigationContentColor = DarwinTouchNeutral1000,
                yearContentColor = DarwinTouchNeutral800,
                disabledYearContentColor = DarwinTouchNeutral400,
                currentYearContentColor = DarwinTouchPrimary,
                selectedYearContentColor = DarwinTouchNeutral0,
                disabledSelectedYearContentColor = DarwinTouchNeutral0,
                selectedYearContainerColor = DarwinTouchPrimary,
                disabledSelectedYearContainerColor = DarwinTouchNeutral400,
                dayContentColor = DarwinTouchNeutral1000,
                disabledDayContentColor = DarwinTouchNeutral400,
                selectedDayContentColor = DarwinTouchNeutral0,
                disabledSelectedDayContentColor = DarwinTouchNeutral0,
                selectedDayContainerColor = DarwinTouchPrimary,
                disabledSelectedDayContainerColor = DarwinTouchNeutral400,
                todayContentColor = DarwinTouchPrimary,
                todayDateBorderColor = DarwinTouchPrimary,
                dayInSelectionRangeContentColor = DarwinTouchNeutral0,
                dayInSelectionRangeContainerColor = DarwinTouchPrimary,
                dividerColor = DarwinTouchNeutral200,
            )
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}