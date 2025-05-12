package eka.dr.intl.patients.presentation.components.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.icons.R
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.atom.TextFieldWrapper

@Composable
fun ReferredByInput(
    referredBy: String,
    onReferredByChange: (String) -> Unit,
    referredByContact: String,
    onReferredByContactChange: (String) -> Unit,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            Row(modifier = Modifier, verticalAlignment = Alignment.Top) {
                IconWrapper(
                    icon = R.drawable.ic_circle_up_right_regular,
                    contentDescription = "Referred by Doctor’s Name",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Top)
                )
            }
        },
        headlineContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextFieldWrapper(
                    value = referredBy,
                    onChange = {
                        onReferredByChange(it)
                    },
                    label = "Referring Doctor’s Name",
                    placeholder = "Doctor’s Name",
                    modifier = Modifier.fillMaxWidth()
                )
                TextFieldWrapper(
                    value = referredByContact,
                    onChange = {
                        onReferredByContactChange(it)
                    },
                    label = "Referring Doctor’s Contact",
                    placeholder = "Doctor’s Contact",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}