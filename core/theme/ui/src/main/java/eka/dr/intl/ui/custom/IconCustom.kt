package eka.dr.intl.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.typography.touchLabelBold
import eka.dr.intl.ui.atom.IconWrapper


@Composable
fun IconCustom(
    icon: Int,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(20.dp),
    iconContainerModifier: Modifier = Modifier
        .size(48.dp)
        .clip(shape = CircleShape)
        .background(DarwinTouchPrimaryBgLight),
    cta: String,
    iconColor: Color = DarwinTouchNeutral1000,
    ctaStyle: TextStyle = touchLabelBold,
    ctaColor: Color = DarwinTouchNeutral800,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = iconContainerModifier
                .clip(shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconWrapper(
                boundingBoxSize = 24.dp,
                icon = icon,
                contentDescription = cta,
                tint = iconColor,
                modifier = iconModifier
            )
        }
        Text(text = cta, style = ctaStyle, color = ctaColor)
    }
}
