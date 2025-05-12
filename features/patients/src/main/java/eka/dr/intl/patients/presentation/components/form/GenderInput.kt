package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral300
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchLabelRegular
import eka.dr.intl.ui.atom.IconWrapper

@ExperimentalMaterial3Api()
@Composable
fun GenderInput(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Male", "Female", "Other")

    val isError = selectedOption.isEmpty()

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            IconWrapper(
                icon = R.drawable.ic_mars_and_venus_sharp_regular,
                contentDescription = "Mobile",
                modifier = Modifier.size(20.dp)
            )
        },
        supportingContent = {
            Text(
                text = "Mandatory",
                modifier = Modifier.padding(start = 16.dp),
                style = touchLabelRegular,
                color = if (isError) DarwinTouchRed else DarwinTouchNeutral800
            )
        },
        headlineContent = {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                options.forEachIndexed { index, option ->
                    SegmentedButton(
                        colors = SegmentedButtonDefaults.colors().copy(
                            activeBorderColor = DarwinTouchPrimary,
                            activeContainerColor = DarwinTouchPrimaryBgLight,
                            activeContentColor = DarwinTouchPrimary,
                            inactiveBorderColor = if (isError) DarwinTouchRed else DarwinTouchNeutral300,
                            inactiveContainerColor = DarwinTouchNeutral0,
                            inactiveContentColor = DarwinTouchNeutral1000
                        ),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = { onOptionSelected(option.lowercase()) },
                        selected = selectedOption == option.lowercase(),
                    ) {
                        Text(text = option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
    )
}