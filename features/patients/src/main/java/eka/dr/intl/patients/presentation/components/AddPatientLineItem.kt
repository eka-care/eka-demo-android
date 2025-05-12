package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyBold
import eka.dr.intl.typography.touchBodyRegular

@Composable
fun AddPatientLineItem(searchText: String, onClick: () -> Unit) {
    if (searchText.isEmpty()) {
        return
    }
    ListItem(
        modifier = Modifier
            .clickable {
            onClick()
        },
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarwinTouchPrimaryBgLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_regular),
                    contentDescription = "Add",
                    tint = DarwinTouchPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp)
                )
            }
        },
        headlineContent = {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Add Patient", style = touchBodyRegular, color = DarwinTouchPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "\"$searchText\"", style = touchBodyBold, color = DarwinTouchPrimary)
            }
        }
    )
}