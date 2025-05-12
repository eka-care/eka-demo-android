package eka.dr.intl.ui.organism

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarCustom(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    navigationIcon: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = DarwinTouchNeutral0,
    titleColor: Color = DarwinTouchNeutral1000,
    iconColor: Color = DarwinTouchNeutral800,
    borderColor: Color = DarwinTouchNeutral200
) {
    TopAppBar(
        title = title,
        modifier = modifier.drawBehind {
            val strokeWidth = 1.dp.toPx()
            val y = size.height - strokeWidth / 2
            drawLine(
                color = borderColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = titleColor,
            navigationIconContentColor = iconColor,
            actionIconContentColor = iconColor,
            scrolledContainerColor = Color.Unspecified
        ),
        scrollBehavior = scrollBehavior
    )
}