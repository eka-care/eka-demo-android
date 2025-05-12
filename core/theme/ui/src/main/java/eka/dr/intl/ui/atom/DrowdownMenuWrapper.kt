package eka.dr.intl.ui.atom

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import eka.dr.intl.icons.R


@Composable
fun <T> DropdownMenuWithDetails(
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    value: String,
    options: List<T>,
    label: String = "",
    placeholder: String = "",
    dropdownMenuItem: @Composable ((item: T, onClick: () -> Unit) -> Unit),
    disabled: Boolean = false,
    isError: Boolean = false,
    colors: TextFieldColors? = null,
    supportingText: String = " ",
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        TextFieldWrapper(
            colors = colors,
            supportingText = supportingText,
            modifier = modifier.pointerInput(value) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        expanded = true
                    }
                }
            },
            enabled = !disabled,
            value = value,
            onChange = {
            },
            placeholder = placeholder,
            label = label,
            keyboardOptions = keyboardOptions,
            trailingIcon = R.drawable.ic_caret_down_solid,
            isError = isError,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                dropdownMenuItem(option) {
                    expanded = false
                }
            }
        }
    }
}
