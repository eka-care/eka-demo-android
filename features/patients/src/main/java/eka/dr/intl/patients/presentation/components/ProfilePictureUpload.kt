package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral400
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.icons.R

@Composable
fun ProfilePictureUpload() {
    Box(
        modifier = Modifier.background(DarwinTouchNeutral0)
            .size(72.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            tint = DarwinTouchNeutral400,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(DarwinTouchNeutral100)
                .align(Alignment.Center)
                .padding(20.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_plus_regular),
            contentDescription = "Add Icon",
            tint = DarwinTouchNeutral0,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(DarwinTouchPrimary)
                .clickable { /* Handle click */ }
                .padding(4.dp)
        )
    }
}