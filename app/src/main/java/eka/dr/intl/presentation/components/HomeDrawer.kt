package eka.dr.intl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import eka.dr.intl.BuildConfig
import eka.dr.intl.common.Urls.Companion.DOC_PROFILE_URL
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.utility.ProfileHelper
import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchYellowBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.icons.R
import eka.dr.intl.presentation.viewModel.DrawerViewModel
import eka.dr.intl.typography.touchBody3Regular
import eka.dr.intl.typography.touchBodyBold
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchCaption2Bold
import eka.dr.intl.typography.touchLabelBold
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps
import eka.dr.intl.ui.molecule.IconButtonWrapper

@Composable
fun HomeDrawer(
    drawerViewModel: DrawerViewModel,
    onLogout: () -> Unit,
    onSwitch: (BusinessEntity) -> Unit,
) {
    LaunchedEffect(Unit) {
        drawerViewModel.getAllWorkspace()
    }

    val versionCode: Int = BuildConfig.VERSION_CODE
    val versionName: String = BuildConfig.VERSION_NAME

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(DarwinTouchPrimaryBgLight)
                .weight(2f)
                .padding(16.dp)
        ) {
            Workspaces(
                workspaces = drawerViewModel.workSpacesUiState.collectAsState().value,
                onSwitch = { switchTo ->
                    drawerViewModel.switchWorkspace(
                        switchToWorkspace = switchTo
                    )
                    onSwitch.invoke(switchTo)
                },
                onLogout = {
                    drawerViewModel.signOutWorkspace(
                        workspace = it,
                        onLogout = onLogout,
                        onSwitch = onSwitch
                    )
                },
            )
        }
        Text(
            style = touchBody3Regular,
            color = DarwinTouchNeutral800,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .padding(top = 16.dp)
                .background(DarwinTouchPrimaryBgLight)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = "App version - $versionName($versionCode)"
        )
    }
}

@Composable
private fun Workspaces(
    workspaces: List<BusinessEntity> = listOf(),
    onSwitch: (BusinessEntity) -> Unit,
    onLogout: (BusinessEntity) -> Unit,
) {

    Column {
        Text(
            text = "Workspace",
            style = touchLabelBold,
            color = DarwinTouchNeutral600,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarwinTouchNeutral0, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
        ) {
            itemsIndexed(workspaces) { index, item ->
                ProfileInfo(
                    businessEntity = item,
                    onSwitch = onSwitch,
                    onLogout = onLogout,
                )
                if (index < workspaces.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.height(1.dp),
                        color = DarwinTouchNeutral200
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfo(
    businessEntity: BusinessEntity,
    onSwitch: (BusinessEntity) -> Unit,
    onLogout: (BusinessEntity) -> Unit,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    val isActive = businessEntity.active

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) {
                if (!isActive) {
                    onSwitch.invoke(businessEntity)
                }
            },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(
                        2.dp,
                        if (isActive) DarwinTouchPrimary else DarwinTouchNeutral0,
                        RoundedCornerShape(50)
                    ),
            ) {
                ProfileImage(
                    ProfileImageProps(
                        oid = businessEntity.oid.toLongOrNull(),
                        url = DOC_PROFILE_URL + businessEntity.oid,
                        initials = ProfileHelper.getInitials(businessEntity.name),
                    )
                )
            }
        },
        headlineContent = {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = businessEntity.name,
                    style = touchBodyBold,
                    color = DarwinTouchNeutral1000,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!businessEntity.specialisation.isNullOrEmpty()) {
                    Text(
                        text = "${businessEntity.specialisation}",
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        trailingContent = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActive) {
                    Text(
                        text = if (isActive) "ACTIVE" else "SIGNED OUT",
                        style = touchCaption2Bold,
                        color = if (isActive) DarwinTouchPrimary else DarwinTouchYellowDark,
                        modifier = Modifier
                            .background(
                                if (isActive) DarwinTouchPrimaryBgLight else DarwinTouchYellowBgLight,
                                RoundedCornerShape(40.dp)
                            )
                            .padding(4.dp, 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButtonWrapper(
                    onClick = {
                        showDropdownMenu = true
                    },
                    icon = R.drawable.ic_ellipsis_vertical_regular,
                    contentDescription = "More options",
                    iconSize = 16.dp

                )
                Box {
                    CustomDropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false },
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.width(120.dp),
                            text = {
                                Text(
                                    text = "Sign out",
                                    style = touchCalloutRegular,
                                    color = DarwinTouchRed
                                )
                            },
                            onClick = {
                                onLogout.invoke(businessEntity)
                                showDropdownMenu = false
                            },
                            leadingIcon = {
                                IconWrapper(
                                    icon = R.drawable.ic_arrow_right_from_bracket_solid,
                                    contentDescription = "Sign out",
                                    tint = DarwinTouchRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun DrawerSections(
    heading: String,
    items: List<DrawerItem>,
    onOptionSelect: (CTA) -> Unit,
) {
    Column {
        Text(
            text = heading,
            style = touchLabelBold,
            color = DarwinTouchNeutral600,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = DarwinTouchNeutral200,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            items.forEachIndexed { index, drawerItem ->
                ListItem(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = {
                                onOptionSelect.invoke(drawerItem.cta)
                            }
                        ),
                    colors = ListItemDefaults.colors(
                        containerColor = DarwinTouchNeutral0
                    ),
                    leadingContent = {
                        IconWrapper(
                            modifier = Modifier.size(20.dp),
                            icon = drawerItem.icon,
                            contentDescription = ""
                        )
                    },
                    headlineContent = {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = drawerItem.label,
                            style = touchBodyRegular,
                            color = DarwinTouchNeutral1000,
                            textAlign = TextAlign.Start
                        )
                    },
                    trailingContent = {
                        IconWrapper(
                            icon = R.drawable.ic_chevron_right_regular,
                            contentDescription = "",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                if (index < items.size - 1) {
                    HorizontalDivider(
                        color = DarwinTouchNeutral200
                    )
                }
            }
        }
    }
}

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (expanded) {
        Popup(
            onDismissRequest = onDismissRequest,
            alignment = Alignment.TopEnd,
            offset = IntOffset(
                x = with(LocalDensity.current) { (-10).dp.toPx().toInt() },
                y = with(LocalDensity.current) { (10).dp.toPx().toInt() }),
            properties = PopupProperties(focusable = true),
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarwinTouchNeutral0, RoundedCornerShape(12.dp))
                    .wrapContentSize()
                    .then(modifier)
            ) {
                content.invoke()
            }
        }
    }
}


private data class DrawerItem(
    val icon: Int,
    val label: String,
    val cta: CTA
)
