package eka.dr.intl.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import eka.dr.intl.R
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.typography.touchBodyBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppSettingUpScreen(showDialog: Boolean = false) {
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pulse))
    if (showDialog) {

        BasicAlertDialog(
            onDismissRequest = { },
            modifier = Modifier
                .width(300.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(DarwinTouchNeutral0)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .height(200.dp),
                    composition = composition.value,
                    iterations = LottieConstants.IterateForever
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Setting up Doctor App",
                    style = touchBodyBold,
                    color = DarwinTouchPrimary,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
