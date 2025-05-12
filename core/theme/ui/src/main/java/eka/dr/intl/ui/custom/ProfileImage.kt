package eka.dr.intl.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchGreenBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchYellowBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.typography.touchBodyBold
import kotlin.random.Random


@Stable
data class RandomColor(
    val text: Color,
    val background: Color,
)

val listOfRandomColors = listOf(
    RandomColor(DarwinTouchYellowDark, DarwinTouchYellowBgDark),
    RandomColor(DarwinTouchGreen, DarwinTouchGreenBgDark),
    RandomColor(DarwinTouchPrimary, DarwinTouchPrimaryBgDark),
    RandomColor(DarwinTouchRed, DarwinTouchRedBgDark),
    RandomColor(DarwinTouchNeutral800, DarwinTouchNeutral100)
)

@Immutable
data class ProfileImageProps(
    val oid: Long? = 0L,
    val url: String,
    val initials: String,
    val size: Dp = 32.dp
)

@Composable
fun ProfileImage(
    data: ProfileImageProps
) {
    var randomColor by remember {
        mutableStateOf(
            listOfRandomColors.random(
                Random(
                    data.oid ?: 0L
                )
            )
        )
    }
    LaunchedEffect(Unit) {
        randomColor = listOfRandomColors.random(Random(data.oid ?: 0L))
    }
    val showFallback = remember { mutableStateOf(data.url.isEmpty()) }
    if (showFallback.value) {
        ProfileFallback(
            ProfileFallbackProps(
                size = data.size,
                initials = data.initials,
                randomColor = randomColor
            )
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.url)
                .crossfade(true)
                .networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.READ_ONLY)
                .diskCacheKey(data.url)
                .build(),
            onError = {
                showFallback.value = true
            },
            contentDescription = data.initials,
            modifier = Modifier
                .size(data.size)
                .clip(CircleShape)
                .border(width = 1.dp, color = DarwinTouchNeutral200, shape = CircleShape)
        )
    }
}

@Immutable
data class ProfileFallbackProps(val size: Dp, val initials: String, val randomColor: RandomColor)

@Composable
fun ProfileFallback(
    data: ProfileFallbackProps
) {
    Box(
        modifier = Modifier
            .size(data.size)
            .background(data.randomColor.background, CircleShape)
            .border(width = 1.dp, color = DarwinTouchNeutral200, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = data.initials,
            style = touchBodyBold,
            color = data.randomColor.text,
        )
    }
}