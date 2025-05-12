package eka.dr.intl.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.typography.touchHeadlineBold
import eka.dr.intl.typography.touchTitle3Bold

@Composable
fun LogoutOptionsBottomSheet(
    onLogout: (dataRetained: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = "Logout options",
            style = touchTitle3Bold,
            color = DarwinTouchNeutral1000
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarwinTouchNeutral0
            ),
            border = BorderStroke(1.dp, DarwinTouchNeutral200),
            content = {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "Logout and keep my data",
                    textAlign = TextAlign.Center,
                    style = touchHeadlineBold,
                    color = DarwinTouchPrimary
                )
            },
            onClick = {
                onLogout(true)
            }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarwinTouchNeutral0
            ),
            content = {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "Logout and clear my local data",
                    textAlign = TextAlign.Center,
                    style = touchCalloutBold,
                    color = DarwinTouchRed
                )
            },
            onClick = {
                onLogout(false)
            }
        )
    }
}