package eka.dr.intl.ui.atom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R


@Composable
fun IconWrapper(
    icon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = DarwinTouchNeutral800,
    boundingBoxSize: Dp = 24.dp,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(boundingBoxSize)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF,showBackground = true)
@Composable
fun IconWrapperPreview24() {
    IconWrapper(
        icon = R.drawable.ic_xmark_regular,
        contentDescription = "Icon",
        modifier = Modifier.size(16.dp),
        tint = Color.Black,
        boundingBoxSize = 24.dp
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun IconWrapperPreview32() {
    IconWrapper(
        icon = R.drawable.ic_plus_regular,
        contentDescription = "Icon",
        modifier = Modifier.size(24.dp),
        tint = Color.Black,
        boundingBoxSize = 32.dp
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun IconWrapperPreview64() {
    IconWrapper(
        icon = R.drawable.ic_magnifying_glass_regular,
        contentDescription = "Icon",
        modifier = Modifier.size(32.dp),
        tint = Color.Black,
        boundingBoxSize = 64.dp
    )
}