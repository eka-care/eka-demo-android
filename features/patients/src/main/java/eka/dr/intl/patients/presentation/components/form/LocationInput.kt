package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R
import eka.dr.intl.ui.atom.DropdownMenuWithDetails
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper

@Composable
fun LocationForm(
    isCityEnabled: Boolean = true,
    isPincodeEnabled: Boolean = true,
    listOfCities: List<String>,
    selectedCity: String,
    pincode: String,
    onCityChange: (String) -> Unit,
    onPincodeChange: (String) -> Unit
) {
    if (!(isPincodeEnabled || isCityEnabled)) return;

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_location_dot_regular,
                contentDescription = "Location",
                modifier = Modifier
                    .size(20.dp)
            )
        },
        headlineContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isPincodeEnabled) {
                    TextFieldWrapper(
                        placeholder = "000000",
                        value = pincode,
                        onChange = {
                            onPincodeChange(it)
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        label = "Pincode",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isCityEnabled) {
                    DropdownMenuWithDetails(
                        label = "City",
                        placeholder = "City",
                        options = listOfCities,
                        disabled = true,
                        value = selectedCity,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = DarwinTouchNeutral200,
                            disabledTextColor = DarwinTouchNeutral1000,
                            disabledTrailingIconColor = DarwinTouchNeutral800,
                            disabledPlaceholderColor = DarwinTouchNeutral800,
                            disabledLabelColor = DarwinTouchNeutral800
                        ),
                        dropdownMenuItem = { it, onClick ->
                            DropdownMenuItem(onClick = {
                                onCityChange(it)
                                onClick()
                            }, text = {
                                Text(text = it)
                            })
                        }
                    )
                }
            }
        }
    )
}

