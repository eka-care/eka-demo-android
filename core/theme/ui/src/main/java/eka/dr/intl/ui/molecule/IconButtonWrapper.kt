package eka.dr.intl.ui.molecule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral400
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgLight
import eka.dr.intl.icons.R
import eka.dr.intl.ui.atom.IconWrapper

enum class IconButtonType {
    FILLED,
    STANDARD,
    OUTLINED,
    TONAL
}

class IconButtonColorsOverride(
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val disabledContentColor: Color? = null,
    val disabledContainerColor: Color? = null
)

@Composable
fun IconButtonWrapper(
    enabled: Boolean = true,
    type: IconButtonType = IconButtonType.STANDARD,
    contentDescription: String = "",
    onClick: () -> Unit,
    colors: IconButtonColorsOverride? = null,
    icon: Int,
    iconSize: Dp = 14.dp,
    borderColor: Color = DarwinTouchNeutral200
) {
    val colorsDefaults = when (type) {
        IconButtonType.FILLED -> IconButtonDefaults.filledIconButtonColors(
            containerColor = DarwinTouchPrimary,
            contentColor = DarwinTouchNeutral0,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = DarwinTouchNeutral50
        )

        IconButtonType.STANDARD -> IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarwinTouchNeutral800,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = Color.Transparent
        )

        IconButtonType.TONAL -> IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = DarwinTouchPrimaryBgLight,
            contentColor = DarwinTouchNeutral1000,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = DarwinTouchNeutral50
        )

        IconButtonType.OUTLINED -> IconButtonDefaults.outlinedIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarwinTouchNeutral800,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = Color.Transparent
        )
    }

    val colorsOverride = colorsDefaults.copy(
        containerColor = if (colors?.containerColor != null) colors.containerColor else colorsDefaults.containerColor,
        contentColor = if (colors?.contentColor != null) colors.contentColor else colorsDefaults.contentColor,
        disabledContentColor = if (colors?.disabledContentColor != null) colors.disabledContentColor else colorsDefaults.disabledContentColor,
        disabledContainerColor = if (colors?.disabledContainerColor != null) colors.disabledContainerColor else colorsDefaults.disabledContainerColor
    )

    val tintColor =
        if (enabled) colorsOverride.contentColor else colorsOverride.disabledContentColor

    when (type) {
        IconButtonType.FILLED -> FilledIconButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier
                .size(40.dp),
            colors = colorsOverride,
        ) {
            IconWrapper(
                modifier = Modifier
                    .size(iconSize),
                icon = icon,
                contentDescription = contentDescription,
                tint = tintColor
            )
        }

        IconButtonType.STANDARD -> IconButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier
                .size(40.dp),
            colors = colorsOverride,
        ) {
            IconWrapper(
                modifier = Modifier
                    .size(iconSize),
                icon = icon,
                contentDescription = contentDescription,
                tint = tintColor
            )
        }

        IconButtonType.OUTLINED -> OutlinedIconButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier
                .size(40.dp),
            border = BorderStroke(1.dp, borderColor),
            colors = colorsOverride
        ) {
            IconWrapper(
                modifier = Modifier
                    .size(iconSize),
                icon = icon,
                contentDescription = contentDescription,
                tint = tintColor,
            )
        }

        IconButtonType.TONAL -> FilledTonalIconButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier
                .size(40.dp),
            colors = colorsOverride,
        ) {
            IconWrapper(
                modifier = Modifier
                    .size(iconSize),
                icon = icon,
                contentDescription = contentDescription,
                tint = tintColor
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IconButtonWrapperPreview() {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButtonWrapper(
                type = IconButtonType.STANDARD,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
            )

            IconButtonWrapper(
                type = IconButtonType.OUTLINED,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
            )

            IconButtonWrapper(
                type = IconButtonType.TONAL,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
            )

            IconButtonWrapper(
                type = IconButtonType.FILLED,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButtonWrapper(
                type = IconButtonType.STANDARD,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
                enabled = false
            )

            IconButtonWrapper(
                type = IconButtonType.OUTLINED,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
                enabled = false
            )

            IconButtonWrapper(
                type = IconButtonType.TONAL,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
                colors = IconButtonColorsOverride(
                    containerColor = DarwinTouchRedBgLight
                ),
                enabled = false
            )

            IconButtonWrapper(
                type = IconButtonType.FILLED,
                contentDescription = "IconButton",
                onClick = {},
                icon = R.drawable.ic_arrow_left_regular,
                enabled = false
            )
        }
    }
}