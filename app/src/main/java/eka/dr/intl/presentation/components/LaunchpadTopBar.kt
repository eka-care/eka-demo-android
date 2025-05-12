package eka.dr.intl.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.Urls.Companion.DOC_PROFILE_URL
import eka.dr.intl.common.utility.ProfileHelper.Companion.getInitials
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.icons.R
import eka.dr.intl.presentation.viewModel.HomeViewModel
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchpadTopBar(
    homeViewModel: HomeViewModel,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    val userTokenData = homeViewModel.userInformation.collectAsState().value
    val salutation = userTokenData?.salutation
    val firstName = userTokenData?.name
    val name = "Hello " + if (salutation.isNullOrEmpty()) firstName else "$salutation $firstName"
    val loggedInUserOid = userTokenData?.oid
    val docProfilePic = DOC_PROFILE_URL + loggedInUserOid
    val initials = getInitials(firstName)

    AppBar(
        containerColor = DarwinTouchPrimaryBgDark,
        title = name,
        borderColor = Color.Transparent,
        navigationIcon = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
                    .clickable(
                        onClick = onAvatarClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true)
                    ),
                contentAlignment = Alignment.Center
            ) {
                ProfileImage(
                    ProfileImageProps(
                        oid = loggedInUserOid?.toLongOrNull(),
                        url = docProfilePic,
                        initials = initials,
                    )
                )
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButtonWrapper(
                    icon = R.drawable.ic_magnifying_glass_regular,
                    onClick = { onSearchClick() },
                    iconSize = 16.dp
                )
            }
        }
    )
}