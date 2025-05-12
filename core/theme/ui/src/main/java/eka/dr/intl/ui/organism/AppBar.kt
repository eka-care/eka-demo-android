package eka.dr.intl.ui.organism

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.typography.touchFootnoteRegular
import eka.dr.intl.typography.touchLargeTitleRegular
import eka.dr.intl.typography.touchTitle1Regular
import eka.dr.intl.typography.touchTitle3Bold
import eka.dr.intl.icons.R
import eka.dr.intl.ui.molecule.IconButtonWrapper

enum class AppBarType {
    CENTER_ALIGNED,
    SMALL,
    MEDIUM,
    LARGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    type: AppBarType = AppBarType.SMALL,
    title: String,
    subTitle: String? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    navigationIcon: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = DarwinTouchNeutral0,
    titleColor: Color = DarwinTouchNeutral1000,
    iconColor: Color = DarwinTouchNeutral800,
    borderColor: Color = DarwinTouchNeutral200
) {
    when (type) {
        AppBarType.CENTER_ALIGNED -> CenterAlignedTopAppBar(
            modifier = modifier.drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height + strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = touchTitle3Bold,
                        color = DarwinTouchNeutral1000,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subTitle.isNullOrEmpty()) {
                        Text(
                            text = subTitle,
                            style = touchFootnoteRegular,
                            color = DarwinTouchNeutral600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = containerColor,
                titleContentColor = titleColor,
                navigationIconContentColor = iconColor,
                actionIconContentColor = iconColor,
                scrolledContainerColor = Color.Unspecified
            ),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon
        )

        AppBarType.SMALL -> TopAppBar(
            title = {
                Column {
                    Text(
                        text = title,
                        style = touchTitle3Bold,
                        color = DarwinTouchNeutral1000,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subTitle.isNullOrEmpty()) {
                        Text(
                            text = subTitle,
                            style = touchFootnoteRegular,
                            color = DarwinTouchNeutral600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            modifier = modifier.drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height + strokeWidth / 2
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

        AppBarType.MEDIUM -> MediumTopAppBar(
            modifier = modifier.drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height + strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = touchTitle1Regular,
                        color = DarwinTouchNeutral1000,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subTitle.isNullOrEmpty()) {
                        Text(
                            text = subTitle,
                            style = touchFootnoteRegular,
                            color = DarwinTouchNeutral600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = containerColor,
                titleContentColor = titleColor,
                navigationIconContentColor = iconColor,
                actionIconContentColor = iconColor,
                scrolledContainerColor = Color.Unspecified
            ),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon
        )

        AppBarType.LARGE -> LargeTopAppBar(
            modifier = modifier.drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height + strokeWidth / 2
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = touchLargeTitleRegular,
                        color = DarwinTouchNeutral1000,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subTitle.isNullOrEmpty()) {
                        Text(
                            text = subTitle,
                            style = touchFootnoteRegular,
                            color = DarwinTouchNeutral600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = containerColor,
                titleContentColor = titleColor,
                navigationIconContentColor = iconColor,
                actionIconContentColor = iconColor,
                scrolledContainerColor = Color.Unspecified
            ),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 720)
@Composable
fun AppBarPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
        Column(
            Modifier.width(360.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppBar(
                title = "Small Header",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
            AppBar(
                type = AppBarType.MEDIUM,
                title = "Medium Header",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
            AppBar(
                type = AppBarType.LARGE,
                title = "Large Header",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
            AppBar(
                type = AppBarType.CENTER_ALIGNED,
                title = "Center Header",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
        }
        Column(
            Modifier.width(360.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppBar(
                title = "Small Header",
                subTitle = "Footnote",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
            AppBar(
                type = AppBarType.LARGE,
                title = "Large Header",
                subTitle = "Footnote",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
            AppBar(
                type = AppBarType.CENTER_ALIGNED,
                title = "Center Header",
                subTitle = "Footnote",
                navigationIcon = {
                    IconButtonWrapper(
                        icon = R.drawable.ic_arrow_left_regular,
                        iconSize = 16.dp,
                        onClick = {},
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrow_left_regular,
                            onClick = { },
                            iconSize = 16.dp,
                        )
                    }
                }
            )
        }
    }
}