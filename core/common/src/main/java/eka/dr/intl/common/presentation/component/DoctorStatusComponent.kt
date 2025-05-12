package eka.dr.intl.common.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import eka.dr.intl.common.R
import eka.dr.intl.common.presentation.state.DocStatus
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchTitle2Regular

sealed class DialogType {
    data object StopConsultation : DialogType()
    data object ShortConversation : DialogType()
}

data class NetworkOverlay(
    val isVisible: Boolean = false,
    val status: NetworkState = NetworkState.ONLINE
)

@Composable
fun DoctorStatusComponent(
    modifier: Modifier = Modifier,
    viewModel: DoctorStatusViewModel,
    backgroundStatus: BackgroundType
) {
    val systemUiController = rememberSystemUiController()
    val currentStatus by viewModel.status.collectAsState()
    val networkOverlay by viewModel.networkOverlay.collectAsState()
    var showDialogType by remember { mutableStateOf<DialogType?>(null) }

    LaunchedEffect(Unit) {
        applyWhiteNavigationBar(systemUiController)
    }

    val heightValue = 80.dp
    var targetHeight = when (currentStatus) {
        is DocStatus.ConsultationStatus -> heightValue
        is DocStatus.NetworkStatus -> heightValue
        is DocStatus.NoneStatus -> 0.dp
    }

    if (networkOverlay.isVisible) {
        targetHeight = heightValue
    }

    val height: Dp by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = tween(1000),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                when (backgroundStatus) {
                    is BackgroundType.Gradient -> Modifier
                        .background(backgroundStatus.brush)

                    is BackgroundType.Solid -> Modifier
                        .background(backgroundStatus.color)
                }
            )
            .height(height)
    ) {
        // Show consultation status if active
        if (currentStatus is DocStatus.ConsultationStatus) {
            ConsultationStatus(
                onClick = {
                    when (it.action) {
                        BotTransitionAction.ON_STOP_CLICK.actionName -> {
                            showDialogType = DialogType.StopConsultation
                        }

                        BotTransitionAction.ON_SHORT_CONVO.actionName -> {
                            showDialogType = DialogType.ShortConversation
                        }
                    }
                },
                modifier = modifier,
                viewModel = viewModel,
                backgroundStatus = backgroundStatus
            )
        }

        // Show network status overlay if visible
        if (networkOverlay.isVisible) {
            NetworkStatus(
                modifier = modifier,
                viewModel = viewModel,
                backgroundStatus = backgroundStatus
            )
        }
    }

    showDialogType?.let {
        when (it) {
            DialogType.StopConsultation -> {
                StopConsultationDialog(
                    onDismiss = { showDialogType = null },
                    onConfirm = {
                        viewModel.stopConsultation()
                        showDialogType = null
                    }
                )
            }

            DialogType.ShortConversation -> {
                ShortConversationDialog(
                    onDismiss = { showDialogType = null }
                )
            }
        }
    }
}

@Composable
fun StopConsultationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = DarwinTouchNeutral50,
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(id = R.string.are_you_done_with_conversation),
                style = touchTitle2Regular,
                color = DarwinTouchNeutral1000
            )
        },
        text = {
            Text(
                stringResource(id = R.string.make_sure_you_record_entire_convo),
                style = touchCalloutRegular,
                color = DarwinTouchNeutral800
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    "Yes, I’m done",
                    color = DarwinTouchPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Not yet",
                    color = DarwinTouchPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
fun ShortConversationDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = DarwinTouchNeutral50,
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(id = R.string.looks_like_your_conversation_is_too_short),
                style = touchTitle2Regular,
                color = DarwinTouchNeutral1000
            )
        },
        text = {
            Text(
                stringResource(id = R.string.for_accurate_analysis_please_ensure_your_microphone),
                style = touchCalloutRegular,
                color = DarwinTouchNeutral800
            )
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Ok, got it",
                    color = DarwinTouchPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

private fun applyWhiteNavigationBar(systemUiController: SystemUiController) {
    systemUiController.setNavigationBarColor(
        color = Color.White,
        darkIcons = true
    )
}