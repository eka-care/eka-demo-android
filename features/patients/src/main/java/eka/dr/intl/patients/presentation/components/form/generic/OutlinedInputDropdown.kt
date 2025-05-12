package eka.care.doctor.patients.presentation.ui.components.form.generic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ui.atom.DropdownMenuWithDetails
import eka.dr.intl.ui.atom.IconWrapper

@Composable
fun OutlinedInputDropdown(
    label: String,
    icon: Int,
    options: List<String>,
    selectedOption: String,
    onChangeOption: (String) -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            Box(modifier = Modifier, contentAlignment = Alignment.TopStart) {
                IconWrapper(
                    icon = icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        headlineContent = {
            DropdownMenuWithDetails(
                label = label,
                disabled = true,
                placeholder = label,
                options = options,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = DarwinTouchNeutral200,
                    disabledTextColor = DarwinTouchNeutral1000,
                    disabledTrailingIconColor = DarwinTouchNeutral800,
                    disabledPlaceholderColor = DarwinTouchNeutral800,
                    disabledLabelColor = DarwinTouchNeutral800
                ),
                modifier = Modifier.fillMaxWidth(),
                value = selectedOption,
                dropdownMenuItem = { it, onClick ->
                    DropdownMenuItem(onClick = {
                        onChangeOption(it)
                        onClick()
                    }, text = {
                        Text(text = it)
                    })
                }
            )
        }
    )
}