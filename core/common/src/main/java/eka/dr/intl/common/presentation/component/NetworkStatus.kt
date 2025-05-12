package eka.dr.intl.common.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.R
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryLight
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.icons.R as Icon

enum class NetworkState {
    ONLINE, OFFLINE, RECONNECTED
}

@Composable
fun NetworkStatus(
    modifier: Modifier = Modifier,
    viewModel: DoctorStatusViewModel,
    backgroundStatus: BackgroundType
) {
    val haptic = LocalHapticFeedback.current
    val networkState by viewModel.networkState.collectAsState()

    val background = when (networkState) {
        NetworkState.ONLINE, NetworkState.RECONNECTED -> {
            DarwinTouchGreen
        }

        NetworkState.OFFLINE -> {
            DarwinTouchNeutral1000
        }
    }
    val icon = when (networkState) {
        NetworkState.ONLINE, NetworkState.RECONNECTED -> {
            Icon.drawable.ic_circle_check_solid
        }

        NetworkState.OFFLINE -> {
            Icon.drawable.ic_wifi_slash_solid
        }
    }
    val text = when (networkState) {
        NetworkState.ONLINE, NetworkState.RECONNECTED -> {
            R.string.you_are_online_now
        }

        NetworkState.OFFLINE -> {
            R.string.you_are_offline
        }
    }
    Column(
        modifier = modifier
            .background(background),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(DarwinTouchNeutral0)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = text),
                style = touchCalloutRegular,
                color = DarwinTouchNeutral0,
                modifier = Modifier.weight(1f)
            )
            if (networkState == NetworkState.OFFLINE) {
                Text(
                    text = stringResource(id = R.string.refresh),
                    style = touchCalloutBold,
                    color = DarwinTouchPrimaryLight,
                    modifier = Modifier.clickable {
                        viewModel.onClickRefresh()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            }
        }
    }
}

sealed class BackgroundType {
    data class Gradient(val brush: Brush) : BackgroundType()
    data class Solid(val color: Color) : BackgroundType()
}