package eka.dr.intl.common.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import eka.dr.intl.icons.R
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.presentation.state.DocStatus
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.ekatheme.color.Blue50
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgLight
import eka.dr.intl.ekatheme.color.FuchsiaViolet100
import kotlinx.coroutines.delay

@Composable
fun EkaBotBackgroundAnimation(
    viewModel: DoctorStatusViewModel
) {
    val offsetX = remember { Animatable(0f) }
    val hasAnimated = remember { mutableStateOf(false) }
    val currentStatus by viewModel.status.collectAsState()
    val backgroundModifier = when (currentStatus) {
        is DocStatus.ConsultationStatus ->
            if ((currentStatus as DocStatus.ConsultationStatus).state == ConsultationState.COMPLETED_ANALYSES) {
                Modifier.background(DarwinTouchGreen)
            } else if ((currentStatus as DocStatus.ConsultationStatus).state == ConsultationState.ERROR_ANALYSING) {
                Modifier.background(DarwinTouchRedBgLight)
            } else {
                Modifier.background(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.4f to Blue50,
                            1f to FuchsiaViolet100
                        )
                    )
                )
            }

        else -> Modifier.background(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.4f to Blue50,
                    1f to FuchsiaViolet100
                )
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        if (!hasAnimated.value) {
            offsetX.animateTo(
                targetValue = 2000f,
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = LinearEasing
                )
            )
            hasAnimated.value = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Box 1 (bottom box with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .then(backgroundModifier)
        )

        // Box 2 (sliding box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .background(DarwinTouchNeutral50)
                .zIndex(1f)
        )
    }
}

@Composable
fun StopIcon(onClick: (CTA) -> Unit) {
    IconButton(onClick = { onClick(CTA(action = BotTransitionAction.ON_STOP_CLICK.actionName)) }) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(DarwinTouchRed),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun PauseResumeIcon(
    onClick: () -> Unit,
    buttonState: PausePlayButtonState
) {
    var icon = R.drawable.ic_pause_solid
    when (buttonState) {
        PausePlayButtonState.PLAY -> {
            icon = R.drawable.ic_pause_solid
        }
        PausePlayButtonState.PAUSE -> {
            icon = R.drawable.ic_play_solid
        }
    }
    IconButton(
        modifier = Modifier
            .size(24.dp)
            .padding(4.dp)
            .clip(CircleShape),
        onClick = {
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            tint = DarwinTouchNeutral800
        )
    }
}

enum class PausePlayButtonState {
    PAUSE,
    PLAY
}

enum class ConsultationState {
    WAITING_TIMER, CONVERSATION, STARTED_ANALYSING, GETTING_SMART_NOTES, WAITING_STATE, COMPLETED_ANALYSES, ERROR_ANALYSING, ERROR_SHORT_DURATION, PAUSED
}

enum class BotTransitionAction(val actionName: String) {
    ON_PAUSE_CLICK("on_pause"),
    ON_PLAY_CLICK("on_play"),
    ON_STOP_CLICK("on_stop"),
    ON_SHORT_CONVO("on_short_convo")
}