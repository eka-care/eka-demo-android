package eka.dr.intl.patients.presentation.components

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.utility.Gender
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.GetByMobileResponse
import eka.dr.intl.patients.naviagtion.PatientDirectoryNavigation
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import eka.dr.intl.patients.utils.Conversions
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.molecule.IconButtonWrapper
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarNative(
    context: Context,
    searchText: String,
    navigation: PatientDirectoryNavigation,
    onSearchTextChanged: (String) -> Unit,
    viewModel: PatientDirectoryViewModel,
    onClick: (PatientEntity?, Boolean) -> Unit,
    showFilters: MutableState<Boolean>,
    onAddPatientClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val results = viewModel.searchResults.collectAsLazyPagingItems()
    val active = viewModel.active.collectAsState(initial = false)
    val from = viewModel.from.collectAsState().value
    val isFromAppointment = from == "appointment"
    val isFromHomepage = from == "homepage"
    val focusRequester = remember { FocusRequester() }


    LaunchedEffect(active.value) {
        if (active.value) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        if (searchText.isNotEmpty()) {
            onSearchTextChanged(searchText)
        }
    }

    val isRegistrationAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.PATIENT_REGISTRATION)
    val isAbhaAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.ABHA)

    val handleClick = remember<(PatientEntity?, Boolean) -> Unit> {
        { it, isFromEkaDir ->
            onClick(it, isFromEkaDir)
        }
    }

    BackHandler(enabled = active.value) {
        if (isFromAppointment || isFromHomepage) {
            onBackClick()
        } else {
            viewModel.setActive(false)
            onSearchTextChanged("")
        }
    }

    SearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = DarwinTouchNeutral50,
            dividerColor = DarwinTouchNeutral200,
            inputFieldColors = SearchBarDefaults.inputFieldColors(
                cursorColor = DarwinTouchNeutral1000,
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        query = searchText,
        placeholder = {
            Text(
                text = "Search / Add patients",
                style = touchBodyRegular,
                color = DarwinTouchNeutral600
            )
        },
        onQueryChange = {
            onSearchTextChanged(it)
        },
        onSearch = {
            onSearchTextChanged(it)
        },
        active = active.value,
        onActiveChange = {
            if (!it) {
                if (isFromAppointment || isFromHomepage) {
                    onBackClick()
                } else {
                    viewModel.setActive(false)
                    onSearchTextChanged("")
                }
            } else {
                viewModel.setActive(it)
            }
        },
        leadingIcon = {
            if (active.value) {
                IconButtonWrapper(
                    icon = R.drawable.ic_arrow_left_regular,
                    onClick = {
                        if (isFromAppointment || isFromHomepage) {
                            onBackClick()
                            return@IconButtonWrapper
                        }
                        viewModel.setActive(false)
                        onSearchTextChanged("")
                    },
                )
            } else {
                IconWrapper(
                    icon = R.drawable.ic_magnifying_glass_regular,
                    tint = DarwinTouchNeutral800,
                    contentDescription = "Search",
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        trailingIcon = {
            if (active.value) {
                if (searchText.isNotEmpty()) {
                    IconButtonWrapper(
                        icon = R.drawable.ic_circle_xmark_solid,
                        onClick = {
                            onSearchTextChanged("")
                        },
                    )
                }
            } else {
                IconToggleButton(
                    checked = showFilters.value,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (showFilters.value) DarwinTouchNeutral100 else DarwinTouchNeutral50),
                    onCheckedChange = {
                        showFilters.value = it
                    }) {
                    IconWrapper(
                        icon = R.drawable.ic_filter_regular,
                        tint = DarwinTouchNeutral800,
                        contentDescription = "Filter",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
    ) {
        if (searchText.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                stickyHeader(key = "search-count-local") {
                    Text(
                        text = "${results.itemCount} patients found",
                        style = touchCalloutRegular,
                        color = DarwinTouchNeutral600,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarwinTouchNeutral50)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(results.itemCount, key = { idx -> "pt_search_${results[idx]?.oid}-${idx}" }) {
                    val currentItem = results[it]
                    if (currentItem != null) {
                        PatientListItem(
                            item = currentItem,
                            searchText = searchText,
                            onClick = { handleClick(currentItem, false) },
                        )
                    }
                }

                if (isRegistrationAllowed) {
                    item {
                        AddPatientLineItem(searchText = searchText, onClick = {
                            onAddPatientClick()
                        })
                    }
                }
                item("empty_space") {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}


fun formatter(data: GetByMobileResponse.Profile): PatientEntity? {
    if (data.personal?.phone?.n.isNullOrEmpty()) return null
    if (data.personal?.gender.isNullOrEmpty()) return null
    if (data.id.isNullOrEmpty()) return null
    val gender =
        data.personal?.gender?.let { Converters().toGenderFromString(it) } ?: Gender.UNKNOWN
    val regex = Regex("[^0-9]")

    return PatientEntity(
        oid = data.id,
        uuid = null,
        name = "${data.personal?.name?.f}",
        age = Conversions.formYYYYMMDDToLong(data.personal?.age?.dob),
        phone = regex.replace(data.personal?.phone?.n ?: "", "").toLongOrNull(),
        gender = gender,
        onApp = data.personal?.onApp == true,
        createdAt = Calendar.getInstance().timeInMillis,
        updatedAt = Calendar.getInstance().timeInMillis,
        archived = false,
        countryCode = regex.replace(data.personal?.phone?.c ?: "", "").toIntOrNull(),
        formData = "[]",
        businessId = OrbiUserManager.getSelectedBusiness() ?: "",
    )
}