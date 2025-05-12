package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R
import eka.dr.intl.patients.presentation.screens.checkIfValidPhoneNumber
import eka.dr.intl.patients.utils.COUNTRY_CODES
import eka.dr.intl.ui.atom.DropdownMenuWithDetails
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper

@Composable
fun MobileNumberInput(
    isPhoneCountryCodeEnabled: Boolean = true,
    code: String = "+91",
    onCodeChange: (String) -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    disabled: Boolean = false,
) {
    val listOfCountryCodes = COUNTRY_CODES.map { "+${it.countryCallingCode}" }
    val regex = Regex("[^0-9]")
    val isIndia = code == "+91"

    val isNumberValid = checkIfValidPhoneNumber(phCode = code, phoneNumber = searchValue.trim())

    val errorMessage = if (!isNumberValid && searchValue.isNotEmpty()) {
        "Invalid Mobile Number"
    } else {
        ""
    }

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_mobile_regular,
                contentDescription = "Mobile",
                modifier = Modifier.size(20.dp)
            )
        },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPhoneCountryCodeEnabled) {
                    DropdownMenuWithDetails(
                        disabled = true,
                        label = "Country Code",
                        placeholder = "",
                        supportingText = " ",
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = DarwinTouchNeutral200,
                            disabledTextColor = DarwinTouchNeutral1000,
                            disabledTrailingIconColor = DarwinTouchNeutral800,
                            disabledPlaceholderColor = DarwinTouchNeutral800,
                            disabledLabelColor = DarwinTouchNeutral800
                        ),
                        options = listOfCountryCodes,
                        modifier = Modifier.width(120.dp),
                        value = code,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        dropdownMenuItem = { it, onClick ->
                            DropdownMenuItem(onClick = {
                                onCodeChange(it)
                                onClick()
                            }, text = {
                                Text(text = it)
                            })
                        }
                    )
                }
                TextFieldWrapper(
                    required = true,
                    enabled = !disabled,
                    label = "Mobile Number",
                    value = searchValue,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onChange = {
                        val value = regex.replace(it, "")
                        if (isIndia) {
                            val last10Digits = value.takeLast(10)
                            onSearchValueChange(last10Digits)
                        } else {
                            onSearchValueChange(value)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isError = !isNumberValid && searchValue.isNotEmpty(),
                    supportingText = errorMessage.ifEmpty { " " }
                )
            }
        }
    )
}