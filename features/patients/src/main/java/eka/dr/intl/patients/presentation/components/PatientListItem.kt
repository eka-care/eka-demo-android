package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.common.utility.DateUtils.Companion.convertLongToDateString
import eka.dr.intl.common.utility.ProfileHelper
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.icons.R
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.utils.PatientDirectoryUtils.Companion.getAnnotationString
import eka.dr.intl.patients.utils.PatientDirectoryUtils.Companion.getProfileImageByOid
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchFootnoteBold
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps

@Composable
fun PatientListItem(
    item: PatientEntity,
    searchText: String,
    modifier: Modifier = Modifier,
    loading: Boolean? = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val name = item.name
    val gender = Converters().fromGenderToString(item.gender)
    val age = DateUtils.getAgeFromYYYYMMDD(convertLongToDateString(item.age))
    val nameInitials = ProfileHelper.getInitials(name = name)
    val profileImage = getProfileImageByOid(item.oid)
    val uhid = item.uhid
    val dirty = item.dirty
    val mobile = if (item.phone != null) "${item.countryCode}${item.phone}" else null
    val isPatientOnApp = item.onApp

    val isPatientNumberAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.PATIENT_MOBILE_NUMBER)

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = DarwinTouchNeutral0
        ),
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple()
        ) {
            onClick()
        },
        leadingContent = {
            ProfileImage(
                ProfileImageProps(
                    oid = item.oid.toLongOrNull(),
                    url = profileImage,
                    initials = nameInitials,
                    size = 40.dp
                )
            )
            if (dirty) {
                Box(
                    modifier = Modifier.absolutePadding(left = 24.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triangle_exclamation_solid),
                        contentDescription = nameInitials,
                        tint = DarwinTouchRed,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
            }
        },
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = getAnnotationString(name, searchText),
                    style = touchBodyRegular,
                    color = DarwinTouchNeutral1000
                )
                if (isPatientOnApp) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_eka_logo_custom),
                        contentDescription = nameInitials,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        },
        supportingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!gender.isNullOrEmpty()) {
                    Text(
                        text = gender.first().uppercase(),
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600
                    )
                }
                if (!gender.isNullOrEmpty() && age.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_solid),
                        contentDescription = "Circle",
                        tint = DarwinTouchNeutral600,
                        modifier = Modifier.size(3.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                if (age.isNotEmpty()) {
                    Text(
                        text = age,
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600
                    )
                }
                if (age.isNotEmpty() && (!mobile.isNullOrEmpty() && isPatientNumberAllowed)) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_solid),
                        contentDescription = "Circle",
                        tint = DarwinTouchNeutral600,
                        modifier = Modifier.size(3.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                if (!mobile.isNullOrEmpty() && isPatientNumberAllowed) {
                    Text(
                        text = getAnnotationString(mobile, searchText, touchFootnoteBold),
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600
                    )
                }
                if ((age.isNotEmpty() || (!mobile.isNullOrEmpty() && isPatientNumberAllowed)) && !uhid.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_solid),
                        contentDescription = "Circle",
                        tint = DarwinTouchNeutral600,
                        modifier = Modifier.size(3.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                if (!uhid.isNullOrEmpty()) {
                    Text(
                        text = getAnnotationString(uhid, searchText, touchFootnoteBold),
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        },
    )
}