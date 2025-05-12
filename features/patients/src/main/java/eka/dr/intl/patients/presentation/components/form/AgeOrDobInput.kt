package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R
import eka.dr.intl.patients.presentation.components.YearPicker
import eka.dr.intl.patients.presentation.components.getAge
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.ui.atom.DatePickerFieldToModal
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeOrDobInput(
    age: Long?,
    dateOfBirth: String,
    onDateOfBirthChange: (String) -> Unit,
    viewModel: AddPatientViewModel,
) {
    if (viewModel.showAgePickerBottomSheet.collectAsState().value) {
        BottomSheet(
            onDismiss = {
                viewModel.updateShowAgePickerBottomSheet(false)
            },
            onAgeChange = {
                viewModel.updateAge(it)
            },
            viewModel = viewModel
        )
    }

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_calendar_regular,
                contentDescription = "Age",
                modifier = Modifier.size(20.dp)
            )
        },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextFieldWrapper(
                    value = if (age != null) getAge(viewModel) else "",
                    onChange = { },
                    label = "Age",
                    placeholder = "MM/DD/YYYY",
                    enabled = false,
                    required = true,
                    supportingText = " ",
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = DarwinTouchNeutral200,
                        disabledTextColor = DarwinTouchNeutral1000,
                        disabledSupportingTextColor = DarwinTouchNeutral800,
                        disabledTrailingIconColor = DarwinTouchNeutral800,
                        disabledPlaceholderColor = DarwinTouchNeutral800,
                        disabledLabelColor = DarwinTouchNeutral800
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(age) {
                            awaitEachGesture {
                                // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                                // in the Initial pass to observe events before the text field consumes them
                                // in the Main pass.
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    viewModel.updateShowAgePickerBottomSheet(true)
                                }
                            }
                        }
                )
                Text(text = "OR", style = touchCalloutBold, color = DarwinTouchNeutral800)
                DatePickerFieldToModal(
                    required = true,
                    modifier = Modifier.weight(1f),
                    selectedDate = dateOfBirth,
                    onDateSelected = { onDateOfBirthChange(it) }
                )
            }
        },
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    onAgeChange: (Long) -> Unit,
    viewModel: AddPatientViewModel
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarwinTouchNeutral0,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxWidth()
    ) {
        YearPicker(onClickExit = onDismiss, onAgeChange = onAgeChange, viewModel = viewModel)
    }
}