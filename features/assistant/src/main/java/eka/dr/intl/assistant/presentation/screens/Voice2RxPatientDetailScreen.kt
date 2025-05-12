package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import eka.dr.intl.icons.R
import eka.dr.intl.assistant.presentation.components.EkaChatBotTopBar
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRow
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRowData
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.assistant.utility.MessageTypeMapping
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600

@Composable
fun Voice2RxPatientDetailScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarwinTouchNeutral0),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        EkaChatBotTopBar(
            hasClinicalNotes = false,
            consultationStarted = true,
            title = "Sudha",
            onClick = {
            }
        )
        ChatsFoundComponent(chatNo = 5)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            item {
                EkaChatPatientsRow(
                    data = EkaChatPatientsRowData(
                        headlineText = " hey this is dfghj",
                        subHeadline = MessageTypeMapping.getSubHeadline(MessageType.TEXT.stringValue),
                        spaceBetweenSubHeadlineAndTime = true,
                        icon = R.drawable.ic_circle_waveform_lines_regular,
                        iconSize = 16,
                        backgroundColor = Color.White
                    ),
                    onClick = {
                    }
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarwinTouchNeutral600)
                )
            }
        }
    }
}
