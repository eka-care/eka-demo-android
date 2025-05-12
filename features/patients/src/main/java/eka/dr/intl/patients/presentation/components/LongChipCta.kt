package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.ui.atom.IconWrapper


@Composable
fun LongChipCta(
    icon: Int,
    cta: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = touchBodyRegular,
    backgroundColor: Color = DarwinTouchNeutral50,
    iconColor: Color = DarwinTouchNeutral800,
    ctaColor: Color = DarwinTouchNeutral1000,
    onClick: () -> Unit,
    rightContent: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconWrapper(
                    modifier = Modifier
                        .size(24.dp),
                    tint = iconColor,
                    icon = icon,
                    contentDescription = cta
                )
                Text(
                    text = cta,
                    style = textStyle,
                    color = ctaColor
                )
            }
            rightContent()
        }
    }
}
