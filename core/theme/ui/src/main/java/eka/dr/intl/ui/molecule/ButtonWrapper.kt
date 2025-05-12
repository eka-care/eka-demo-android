package eka.dr.intl.ui.molecule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral400
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.icons.R
import eka.dr.intl.ui.atom.IconWrapper

enum class ButtonWrapperType {
    FILLED,
    OUTLINED,
    TEXT,
    ELEVATED,
    TONAL
}

class ButtonWrapperColorsOverride(
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val disabledContentColor: Color? = null,
    val disabledContainerColor: Color? = null
)

@Composable
fun ButtonWrapper(
    modifier: Modifier = Modifier,
    type: ButtonWrapperType = ButtonWrapperType.FILLED,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonWrapperColorsOverride? = null,
    iconSize: Dp = 14.dp,
    icon: Int? = null,
    borderColor: Color = DarwinTouchNeutral200
) {
    val colorsDefaults = when (type) {
        ButtonWrapperType.FILLED -> ButtonDefaults.buttonColors(
            containerColor = DarwinTouchPrimary,
            contentColor = DarwinTouchNeutral0,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = DarwinTouchNeutral100
        )

        ButtonWrapperType.OUTLINED -> ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarwinTouchPrimary,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = Color.Transparent
        )

        ButtonWrapperType.TEXT -> ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarwinTouchPrimary,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = Color.Transparent
        )

        ButtonWrapperType.ELEVATED -> ButtonDefaults.elevatedButtonColors(
            containerColor = Color.Transparent,
            contentColor = DarwinTouchPrimary,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = DarwinTouchNeutral100
        )

        ButtonWrapperType.TONAL -> ButtonDefaults.filledTonalButtonColors(
            containerColor = DarwinTouchPrimaryBgLight,
            contentColor = DarwinTouchNeutral800,
            disabledContentColor = DarwinTouchNeutral400,
            disabledContainerColor = DarwinTouchNeutral100
        )
    }

    val colorsOverride = colorsDefaults.copy(
        containerColor = if (colors?.containerColor != null) colors.containerColor else colorsDefaults.containerColor,
        contentColor = if (colors?.contentColor != null) colors.contentColor else colorsDefaults.contentColor,
        disabledContentColor = if (colors?.disabledContentColor != null) colors.disabledContentColor else colorsDefaults.disabledContentColor,
        disabledContainerColor = if (colors?.disabledContainerColor != null) colors.disabledContainerColor else colorsDefaults.disabledContainerColor
    )

    val iconColor =
        if (enabled) colorsOverride.contentColor else colorsOverride.disabledContentColor

    when (type) {
        ButtonWrapperType.FILLED -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colorsOverride,
            contentPadding = PaddingValues(0.dp),
        ) {
            ButtonContent(icon, text, iconSize, iconColor)
        }

        ButtonWrapperType.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colorsOverride,
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, borderColor),
        ) {
            ButtonContent(icon, text, iconSize, iconColor)

        }

        ButtonWrapperType.TEXT -> TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colorsOverride,
            contentPadding = PaddingValues(0.dp),
        ) {
            ButtonContent(icon, text, iconSize, iconColor)
        }

        ButtonWrapperType.ELEVATED -> ElevatedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colorsOverride,
            contentPadding = PaddingValues(0.dp),
        ) {
            ButtonContent(icon, text, iconSize, iconColor)
        }

        ButtonWrapperType.TONAL -> FilledTonalButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = colorsOverride,
            contentPadding = PaddingValues(0.dp),
        ) {
            ButtonContent(icon, text, iconSize, iconColor)
        }
    }
}

@Composable
private fun ButtonContent(icon: Int? = null, text: String, iconSize: Dp, iconColor: Color) {
    Row(
        modifier = Modifier
            .padding(
                start = if (icon == null) 16.dp else 12.dp,
                top = 10.dp,
                end = 16.dp,
                bottom = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) {
            IconWrapper(
                icon = icon,
                contentDescription = "",
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
        Text(
            text = text,
            style = touchCalloutBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, widthDp = 720)
@Composable
fun ButtonWrapperPreview() {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ButtonWrapper(
                text = "Button",
                onClick = { },
            )
            ButtonWrapper(
                type = ButtonWrapperType.OUTLINED,
                text = "Button",
                onClick = { },
            )

            ButtonWrapper(
                type = ButtonWrapperType.TEXT,
                text = "Button",
                onClick = { },
            )

            ButtonWrapper(
                type = ButtonWrapperType.TONAL,
                text = "Button",
                onClick = { },
            )

            ButtonWrapper(
                type = ButtonWrapperType.ELEVATED,
                text = "Button",
                onClick = { },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            ButtonWrapper(
                text = "Button",
                onClick = { },
                enabled = false
            )
            ButtonWrapper(
                type = ButtonWrapperType.OUTLINED,
                text = "Button",
                onClick = { },
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.TEXT,
                text = "Button",
                onClick = { },
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.TONAL,
                text = "Button",
                onClick = { },
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.ELEVATED,
                text = "Button",
                onClick = { },
                enabled = false
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            ButtonWrapper(
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular
            )
            ButtonWrapper(
                type = ButtonWrapperType.OUTLINED,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular
            )

            ButtonWrapper(
                type = ButtonWrapperType.TEXT,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular
            )

            ButtonWrapper(
                type = ButtonWrapperType.TONAL,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular

            )

            ButtonWrapper(
                type = ButtonWrapperType.ELEVATED,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ButtonWrapper(
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular,
                enabled = false
            )
            ButtonWrapper(
                type = ButtonWrapperType.OUTLINED,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular,
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.TEXT,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular,
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.TONAL,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular,
                enabled = false
            )

            ButtonWrapper(
                type = ButtonWrapperType.ELEVATED,
                text = "Button",
                onClick = { },
                icon = R.drawable.ic_xmark_regular,
                enabled = false
            )
        }
    }
}