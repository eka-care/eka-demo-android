package eka.dr.intl.patients.presentation.model

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800

data class ActionGroups(val title: String, val actions: List<ActionItem>)
data class ActionItem(
    val text: String,
    val icon: Int,
    val textColor: Color = DarwinTouchNeutral1000,
    val iconColor: Color = DarwinTouchNeutral800,
    val iconModifier: Modifier = Modifier
        .size(16.dp),
    val onClick: () -> Unit,
)