package eka.dr.intl.common.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.icons.R
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.presentation.state.DocStatus
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.typography.touchCalloutBold
import kotlinx.coroutines.delay

@Composable
fun ConsultationStatus(
    onClick: (CTA) -> Unit,
    modifier: Modifier,
    viewModel: DoctorStatusViewModel,
    backgroundStatus: BackgroundType,
) {
    var dotCount by remember { mutableIntStateOf(0) }
    var timer by remember { mutableIntStateOf(3) }
    val elapsedSeconds by viewModel.elapsedSeconds
    var isRunning by remember { mutableStateOf(false) }
    val currentStatus by viewModel.status.collectAsState()
    val consultationStatusState = (currentStatus as? DocStatus.ConsultationStatus)?.state

    LaunchedEffect(true) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    LaunchedEffect(timer) {
        while (timer > 1) {
            delay(1000L)
            timer -= 1
        }
        if (timer == 1) {
            delay(1000L)
            if (consultationStatusState == ConsultationState.WAITING_TIMER) {
                viewModel.updateConsultationState(
                    DocStatus.ConsultationStatus(state = ConsultationState.CONVERSATION)
                )
            }
            viewModel.startRecordingTimer()
            isRunning = true
        }
    }

    val text = when (val status = currentStatus) {
        is DocStatus.ConsultationStatus -> when (status.state) {
            ConsultationState.WAITING_TIMER -> "Starting in $timer${".".repeat(dotCount)}"
            ConsultationState.PAUSED, ConsultationState.CONVERSATION -> "${viewModel.consultationData?.voiceContext}  •  ${
                String.format(
                    "%02d:%02d",
                    elapsedSeconds / 60,
                    elapsedSeconds % 60
                )
            }"

            ConsultationState.STARTED_ANALYSING -> "Analysing conversation${".".repeat(dotCount)}"
            ConsultationState.GETTING_SMART_NOTES -> "Generating smart notes${".".repeat(dotCount)}"
            ConsultationState.WAITING_STATE -> "This might take some time. Please wait${
                ".".repeat(
                    dotCount
                )
            }${" ".repeat(3 - dotCount)}"

            ConsultationState.COMPLETED_ANALYSES -> "Smart notes are ready!"
            ConsultationState.ERROR_ANALYSING -> "Oh no! Audio analysis failed."
            else -> ""
        }

        else -> "Consultation not started"
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        EkaBotBackgroundAnimation(
            viewModel = viewModel,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(Color.Transparent)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (currentStatus is DocStatus.ConsultationStatus) {
                    ConfigurePausePlayButton(
                        consultationStatusState = consultationStatusState,
                        viewModel = viewModel
                    )
                }
                if (currentStatus is DocStatus.ConsultationStatus &&
                    consultationStatusState == ConsultationState.COMPLETED_ANALYSES
                ) {
                    IconButton(onClick = {
                    }) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_file_waveform_regular
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = DarwinTouchNeutral0
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier) {
                    if (consultationStatusState == ConsultationState.WAITING_TIMER ||
                        consultationStatusState == ConsultationState.CONVERSATION
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_circle_waveform_lines_regular),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (consultationStatusState == ConsultationState.ERROR_ANALYSING) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_circle_exclamation_solid),
                            colorFilter = ColorFilter.tint(DarwinTouchRed),
                            contentDescription = "doc_Assist",
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (consultationStatusState != ConsultationState.COMPLETED_ANALYSES) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_doc_assist_filled_custom),
                            contentDescription = "doc_Assist",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = text,
                        style = touchCalloutBold,
                        color = if (consultationStatusState == ConsultationState.COMPLETED_ANALYSES) DarwinTouchNeutral0 else DarwinTouchNeutral1000
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (shouldShowStopIcon(consultationStatusState)) {
                    StopIcon(onClick = {
                        isRunning = false
                        onClick(CTA(action = BotTransitionAction.ON_STOP_CLICK.actionName))
                    })
                } else {
                    Box(modifier = Modifier.size(24.dp))
                }
            }
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .then(
                        when (backgroundStatus) {
                            is BackgroundType.Gradient -> Modifier
                                .background(backgroundStatus.brush)

                            is BackgroundType.Solid -> Modifier
                                .background(backgroundStatus.color)
                        }
                    )
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

fun shouldShowStopIcon(currentStatus: ConsultationState?): Boolean {
    return currentStatus == ConsultationState.PAUSED || currentStatus == ConsultationState.CONVERSATION
}

@Composable
fun ConfigurePausePlayButton(
    consultationStatusState: ConsultationState?,
    viewModel: DoctorStatusViewModel
) {
    when (consultationStatusState) {
        ConsultationState.PAUSED -> {
            PauseResumeIcon(
                onClick = {
                    viewModel.resumeConsultation()
                },
                buttonState = PausePlayButtonState.PAUSE
            )
        }
        ConsultationState.CONVERSATION -> {
            PauseResumeIcon(
                onClick = {
                    viewModel.pauseConsultation()
                },
                buttonState = PausePlayButtonState.PLAY
            )
        }
        else -> {}
    }
}
