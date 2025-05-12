package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import eka.dr.intl.assistant.presentation.models.SuggestionModel
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral300
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.typography.touchCalloutRegular

@Composable
fun Suggestion(
    suggestion: SuggestionModel,
    onSuggestionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .clickable {
                onSuggestionClick()
            }
            .background(color = DarwinTouchNeutral0, shape = RoundedCornerShape(size = 8.dp))
            .border(
                width = 1.dp,
                color = DarwinTouchNeutral300,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(start = 16.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
    ) {
        Text(
            text = suggestion.label,
            style = touchCalloutRegular,
            color = DarwinTouchPrimary
        )
    }
}