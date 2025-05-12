package eka.dr.intl.patients.presentation.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.icons.R
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.naviagtion.PatientDirectoryNavigation
import eka.dr.intl.patients.presentation.viewModels.Filter
import eka.dr.intl.patients.presentation.viewModels.PatientCount
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import eka.dr.intl.typography.touchCalloutBold
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
fun PatientDirectoryScreenContent(
    context: Context,
    paddingValues: PaddingValues,
    patientOid: String? = null,
    isAbha: Boolean? = false,
    onBackClick: () -> Unit,
    navigation: PatientDirectoryNavigation,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val viewModel: PatientDirectoryViewModel = koinViewModel()
    val patientList = viewModel.searchResults.collectAsLazyPagingItems()
    val count = viewModel.count.collectAsState(PatientCount())
    val from = viewModel.from.collectAsState(initial = "").value

    val selectedPatient = remember { mutableStateOf<PatientEntity?>(null) }
    val selectedFilter = remember { mutableStateOf(Filter.ALL) }
    val showFilters = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val searchText = remember { mutableStateOf("") }

    val filterList = listOf(
        FilterActions(
            label = "All",
            action = {
                viewModel.filterPatients(Filter.ALL)
                selectedFilter.value = Filter.ALL
            },
            key = Filter.ALL,
        ),
        FilterActions(
            label = "UHID (${count.value.uhidPatientCount})", action = {
                if (selectedFilter.value == Filter.UHID) {
                    viewModel.filterPatients(Filter.ALL)
                    selectedFilter.value = Filter.ALL
                } else {
                    viewModel.filterPatients(Filter.UHID)
                    selectedFilter.value = Filter.UHID
                }
            }, key = Filter.UHID
        ),
        FilterActions(
            label = "On App (${count.value.onAppPatientCount})", action = {
                if (selectedFilter.value == Filter.ON_APP) {
                    viewModel.filterPatients(Filter.ALL)
                    selectedFilter.value = Filter.ALL
                } else {
                    viewModel.filterPatients(Filter.ON_APP)
                    selectedFilter.value = Filter.ON_APP
                }
            }, key = Filter.ON_APP
        ),
    )

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(patientOid) {
        if (patientOid != null) {
            selectedPatient.value = viewModel.getPatientByOid(patientOid)
        }
    }

    val active = viewModel.active.collectAsState()

    LaunchedEffect(active.value) {
        if (!active.value && searchText.value.isNotEmpty()) {
            searchText.value = ""
            viewModel.searchPatient("")
        }
    }

    if (active.value) {
        SearchBarNative(
            context,
            searchText = searchText.value,
            onSearchTextChanged = {
                searchText.value = it
                viewModel.searchPatient(it)
            },
            onClick = { item, isFromEkaDir ->
                if (isFromEkaDir && item != null) {
                    navigation.navigateToAddPatient(
                        pid = item.oid,
                        from = if (isFromEkaDir) "appointment" else "",
                    )
                } else {
                    item?.oid?.let {
                        navigation.navigateToPatientActionsScreen(pid = it)
                        selectedPatient.value = item
                    }
                }
            },
            viewModel = viewModel,
            showFilters = showFilters,
            onBackClick = onBackClick,
            navigation = navigation,
            onAddPatientClick = {
                val params = JSONObject()
                params.put("isAbha", isAbha)
                if (from == "appointment") {
                    params.put("from", "appointment")
                    navigation.navigateToAddPatientWithInputText(
                        inputText = searchText.value,
                        from = from
                    )
                } else {
                    navigation.navigateToAddPatientWithInputText(
                        inputText = searchText.value
                    )
                }
            })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = maxOf(paddingValues.calculateTopPadding() - 36.dp, 0.dp),
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .background(DarwinTouchNeutral0)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                SearchBarNative(
                    context,
                    searchText = searchText.value,
                    onSearchTextChanged = {
                        searchText.value = it
                        viewModel.searchPatient(it)
                    },
                    onClick = { _, _ ->
                    },
                    viewModel = viewModel,
                    showFilters = showFilters,
                    onBackClick = onBackClick,
                    navigation = navigation,
                    onAddPatientClick = {
                        val params = JSONObject()
                        params.put("isAbha", isAbha)
                        if (from == "appointment") {
                            params.put("from", "appointment")
                            navigation.navigateToAddPatientWithInputText(
                                inputText = searchText.value,
                                from = from
                            )
                        } else {
                            navigation.navigateToAddPatientWithInputText(
                                inputText = searchText.value
                            )
                        }
                    })
                AnimatedVisibility(
                    visible = showFilters.value,
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filterList.size) { item ->
                            val currentItem = filterList[item]
                            val selected = selectedFilter.value == currentItem.key
                            FilterChip(
                                selected = selected, onClick = {
                                    currentItem.action()
                                }, label = {
                                    if (currentItem.showOnlyIcon == true && currentItem.icon != null && currentItem.iconColor != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = currentItem.icon),
                                                contentDescription = currentItem.label,
                                                tint = currentItem.iconColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.size(4.dp))
                                            Text(
                                                text = currentItem.label,
                                                style = touchCalloutBold,
                                                color = DarwinTouchNeutral800
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = currentItem.label,
                                            style = touchCalloutBold,
                                            color = DarwinTouchNeutral800
                                        )
                                    }
                                }, colors = FilterChipDefaults.filterChipColors().copy(
                                    containerColor = DarwinTouchNeutral0,
                                    selectedContainerColor = DarwinTouchPrimaryBgLight
                                ), trailingIcon = {
                                    if (selected) Icon(
                                        painter = painterResource(id = R.drawable.ic_xmark_regular),
                                        contentDescription = "X-mark Icon",
                                        tint = DarwinTouchNeutral1000,
                                        modifier = Modifier.size(16.dp)
                                    ) else {
                                        if (filterList[item].hasBottomSheet == true) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_caret_down_solid),
                                                contentDescription = "Caret Down Icon",
                                                tint = DarwinTouchNeutral1000,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                })
                        }
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarwinTouchNeutral0)
                    .windowInsetsPadding(WindowInsets.ime),
            ) {
                if (patientList.itemCount == 0) {
                    item("empty_state_pt_dir") {
                        PatientSearchEmptyState(isBottomSheet = true)
                    }
                } else {
                    items(
                        patientList.itemCount,
                        key = { idx -> "pt_dir_${patientList[idx]?.oid}-${idx}" }) { idx ->
                        val currentItem = patientList[idx]
                        if (currentItem != null) {
                            PatientListItem(
                                item = currentItem,
                                searchText = searchText.value,
                                onClick = {
                                    selectedPatient.value = currentItem
                                    navigation.navigateToPatientActionsScreen(
                                        pid = currentItem.oid
                                    )
                                },
                            )
                        }
                    }
                    item("empty_space") {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}


data class FilterActions(
    val label: String,
    val action: () -> Unit,
    val count: Int? = null,
    val hasBottomSheet: Boolean? = false,
    val key: Filter,
    val icon: Int? = null,
    val iconColor: Color? = null,
    val showOnlyIcon: Boolean? = false
)