package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyBold
import eka.dr.intl.typography.touchBodyRegular

@Composable
fun PatientSearchEmptyState(
    isBottomSheet: Boolean = false
) {
    val label = if (isBottomSheet) "Patient not found"
    else "Tap on “+” button to add a patient"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_pt_dir),
            modifier = Modifier.size(92.dp),
            contentDescription = "Empty state patient directory"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "View & manage your patients here.",
            style = touchBodyRegular,
            color = DarwinTouchNeutral1000
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            style = touchBodyBold,
            color = DarwinTouchNeutral600
        )
    }
}