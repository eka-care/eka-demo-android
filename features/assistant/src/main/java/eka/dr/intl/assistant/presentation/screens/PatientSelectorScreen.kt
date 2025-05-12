package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.paging.compose.collectAsLazyPagingItems
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.presentation.components.PatientListItem
import eka.dr.intl.patients.presentation.components.PatientSearchEmptyState
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import eka.dr.intl.typography.touchBodyRegular
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientSelectorScreen(
    onClose: () -> Unit,
    onPatientSelected: (PatientEntity) -> Unit
) {
    val viewModel: PatientDirectoryViewModel = koinViewModel()
    val patientList = viewModel.searchResults.collectAsLazyPagingItems()
    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        searchText = ""
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = searchText,
        onQueryChange = {
            searchText = it
            viewModel.searchPatient(it)
        },
        onSearch = {},
        active = true,
        shape = RectangleShape,
        colors = SearchBarDefaults.colors(
            containerColor = Color.White,
            dividerColor = DarwinTouchNeutral200,
        ),
        leadingIcon = {
            IconButton(
                onClick = onClose,
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        tint = DarwinTouchNeutral1000,
                        contentDescription = "Back"
                    )
                }
            )
        },
        placeholder = {
            Text(
                text = "Search patient...",
                style = touchBodyRegular,
                color = DarwinTouchNeutral600
            )
        },
        onActiveChange = { },
        content = {
            if (patientList.itemCount == 0) {
                PatientSearchEmptyState()
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarwinTouchNeutral0)
                    .windowInsetsPadding(WindowInsets.ime),
            ) {
                items(
                    patientList.itemCount,
                    key = { idx -> "pt_dir_${patientList[idx]?.oid}-${idx}" }) { idx ->
                    val currentItem = patientList[idx]
                    if (currentItem != null) {
                        PatientListItem(
                            item = currentItem,
                            searchText = "",
                            onClick = {
                                onPatientSelected(currentItem)
                            },
                        )
                    }
                }
            }
        }
    )
}