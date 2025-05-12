package eka.dr.intl.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral400
import eka.dr.intl.presentation.screens.HomeClick
import eka.dr.intl.typography.touchBodyBold
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.ui.molecule.ButtonWrapper

data class MedicalCard(
    val id: String,
    val iconResId: Int,
    val title: String,
    val description: String,
    val buttonText: String,
    val clickAction: HomeClick
)

@Composable
fun HomeCardContent(card: MedicalCard, onClick: (CTA) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClick(CTA(action = card.clickAction.value))
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = DarwinTouchNeutral0)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(card.iconResId),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = card.title,
                color = DarwinTouchNeutral1000,
                style = touchBodyBold
            )
            Text(
                text = card.description,
                style = touchCalloutRegular,
                color = DarwinTouchNeutral400
            )
            Spacer(modifier = Modifier.height(8.dp))
            ButtonWrapper(
                text = card.buttonText,
                onClick = { onClick(CTA(action = card.clickAction.value)) }
            )
        }
    }
}