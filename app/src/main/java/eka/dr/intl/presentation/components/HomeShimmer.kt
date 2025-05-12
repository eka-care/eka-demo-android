package eka.dr.intl.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.ui.molecule.BorderCard

@Composable
@Preview
fun HomeShimmer() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = DarwinTouchPrimaryBgDark,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Column(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (j in 0 until 3) {
                                HomeGridShimmerItem(Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
        HomeCardShimmer()
        HomeCardShimmer()
    }
}

@Composable
private fun HomeGridShimmerItem(modifier: Modifier) {
    BorderCard(
        modifier = modifier,
        background = Transparent,
        border = BorderStroke(0.dp, Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shimmer()
                    .background(
                        color = DarwinTouchNeutral0,
                        RoundedCornerShape(50)
                    )
            ){ }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
                    .shimmer()
                    .background(
                        color = DarwinTouchNeutral0,
                        RoundedCornerShape(50)
                    )
            ){ }
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(16.dp)
                    .shimmer()
                    .background(
                        color = DarwinTouchNeutral0,
                        RoundedCornerShape(50)
                    )
            ){ }
        }
    }
}

@Composable
fun HomeCardShimmer() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(16.dp)
                .shimmer()
                .background(
                    color = DarwinTouchNeutral50,
                    RoundedCornerShape(50)
                )
        ){ }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(16.dp)
                .shimmer()
                .background(
                    color = DarwinTouchNeutral50,
                    RoundedCornerShape(50)
                )
        ){ }
        Spacer(modifier = Modifier.height(16.dp))
        BorderCard(
            background = DarwinTouchNeutral0,
            border = BorderStroke(0.dp, Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(16.dp)
                        .shimmer()
                        .background(
                            color = DarwinTouchNeutral50,
                            RoundedCornerShape(50)
                        )
                ){ }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(16.dp)
                        .shimmer()
                        .background(
                            color = DarwinTouchNeutral50,
                            RoundedCornerShape(50)
                        )
                ){ }
                Spacer(modifier = Modifier.height(72.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(44.dp)
                        .shimmer()
                        .background(
                            color = DarwinTouchNeutral50,
                            RoundedCornerShape(50)
                        )
                ){ }
            }
        }
    }
}