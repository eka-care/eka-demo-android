package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.assistant.R
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.icons.R as st
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchTitle3Bold
import org.json.JSONObject

@Composable
fun Voice2RxInitialBottomSheet(onClick :(CTA) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.how_would_you_like_to_continue),
            style = touchTitle3Bold,
            color = DarwinTouchNeutral1000,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        EkaChatBotScreenComponent(
            icon = st.drawable.ic_circle_waveform_lines_regular,
            headline = stringResource(id = R.string.conversation_mode),
            subText = stringResource(id = R.string.docAssist_will_listen_to_your_conversation),
            iconSize = 16.dp,
            headlineStyle = touchBodyRegular,
            trailingIcon = true,
            onClick = {
                val params = JSONObject()
                params.put("type", "conversation")
                onClick(CTA(action = ActionType.ON_VOICE_TO_RX_CONVERSATION_MODE.stringValue))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        EkaChatBotScreenComponent(
            icon = st.drawable.ic_podcast_regular,
            headline = stringResource(id = R.string.dictation_mode),
            subText = stringResource(id = R.string.dictates_your_notes_to_docAssist_and_create),
            iconSize = 16.dp,
            headlineStyle = touchBodyRegular,
            trailingIcon = true,
            onClick = {
                val params = JSONObject()
                params.put("type", "dictation")
                onClick(CTA(action = ActionType.ON_VOICE_TO_RX_DICTATION_MODE.stringValue))
            }
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}