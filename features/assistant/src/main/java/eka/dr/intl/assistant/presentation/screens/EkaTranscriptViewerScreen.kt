package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import eka.dr.intl.assistant.navigation.ChatTranscriptNavModel
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkaTranscriptViewerScreen(
    navData: ChatTranscriptNavModel,
    onBackClick: () -> Unit
) {
    val transcript = navData.transcript
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            borderColor = Color.Transparent,
            containerColor = Color.Transparent,
            title = "Transcript",
            navigationIcon = {
                IconButtonWrapper(
                    onClick = onBackClick,
                    icon = R.drawable.ic_arrow_left_regular,
                    iconSize = 16.dp
                )
            }
        )
        if (transcript.isNullOrEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No text to Display", style = touchBodyRegular, color = DarwinTouchNeutral1000)
            }
        } else {
            MarkdownText(
                modifier = Modifier
                    .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                markdown = transcript,
                truncateOnTextOverflow = true,
                enableSoftBreakAddsNewLine = true,
                style = touchBodyRegular,
                color = DarwinTouchNeutral1000
            )
        }
    }
}