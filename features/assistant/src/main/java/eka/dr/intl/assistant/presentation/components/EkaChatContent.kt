package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.assistant.R
import eka.dr.intl.icons.R as st
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.typography.touchBodyBold

@Composable
fun EkaChatContent(
    modifier: Modifier = Modifier,
    onClick: (CTA) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(id = st.drawable.ic_doc_assist_filled_custom),
            contentDescription = null,
            modifier = Modifier
                .width(36.dp)
                .height(29.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        EkaChatBotScreenComponent(
            icon = st.drawable.ic_messages_regular,
            headline = stringResource(id = R.string.ask_anything_from_doc_asist),
            subText = stringResource(id = R.string.medical_facts_check),
            iconSize = 24.dp,
            headlineStyle = touchBodyBold,
            onClick = { }
        )
        Spacer(modifier = Modifier.height(8.dp))
        EkaChatBotScreenComponent(
            icon = st.drawable.ic_circle_waveform_lines_regular,
            headline = stringResource(id = R.string.create_medical_document),
            subText = stringResource(id = R.string.docAssist_ai_can_either_listen),
            iconSize = 20.dp,
            headlineStyle = touchBodyBold,
            onClick = { onClick(CTA(action = ActionType.ON_VOICE_2_RX_CLICK.stringValue)) }
        )
    }
}