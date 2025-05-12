package eka.dr.intl.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import eka.dr.intl.icons.R
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.Purple500
import eka.dr.intl.presentation.components.HomeCardContent
import eka.dr.intl.presentation.components.HomeShimmer
import eka.dr.intl.presentation.components.LaunchpadTopBar
import eka.dr.intl.presentation.components.LogoutOptionsBottomSheet
import eka.dr.intl.presentation.components.MedicalCard
import eka.dr.intl.presentation.states.HomeBottomSheetState
import eka.dr.intl.presentation.states.HomeScreenUiState
import eka.dr.intl.presentation.viewModel.HomeViewModel
import kotlinx.coroutines.launch

enum class HomeClick(val value: String) {
    SEARCH_CLICK("search_click"),
    EKA_SCRIBE_CLICK("eka_scribe_click"),
    DOC_ASSIST_CLICK("doc_assist_click"),
    SUPPORT_CLICK("support_click")
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    drawerState: DrawerState,
    ekaChatViewModel: EkaChatViewModel,
    onClick : (cta: CTA) -> Unit,
) {
    val state by homeViewModel.homeScreenUiState.collectAsState()
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LaunchpadTopBar(
                onAvatarClick = {
                    scope.launch { drawerState.open() }
                },
                onSearchClick = {onClick(CTA(action = HomeClick.SEARCH_CLICK.value))},
                homeViewModel = homeViewModel
            )
        }
    ) { contentPadding ->

        AnimatedContent(
            modifier = Modifier
                .background(DarwinTouchPrimaryBgDark)
                .padding(contentPadding),
            targetState = state,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(700, delayMillis = 90)
                ).togetherWith(
                    fadeOut(animationSpec = tween(700))
                )
            },
            label = ""
        ) {
            when (it) {
                is HomeScreenUiState.StateError -> {

                }

                is HomeScreenUiState.StateLoading -> {
                    HomeShimmer()
                }

                is HomeScreenUiState.StateSuccess -> {
                    Content(
                        homeViewModel = homeViewModel,
                        state = state,
                        ekaChatViewModel = ekaChatViewModel,
                        onClick = onClick
                    )
                    if (homeViewModel.homeBottomSheetState.value != null) {
                        BottomSheet(
                            homeViewModel = homeViewModel,
                            onCloseSheet = {
                                homeViewModel.homeBottomSheetState.value = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    homeViewModel: HomeViewModel,
    onCloseSheet: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val bottomSheetState = homeViewModel.homeBottomSheetState.value

    ModalBottomSheet(
        onDismissRequest = {
            onCloseSheet.invoke()
        },
        sheetState = sheetState,
        containerColor = DarwinTouchNeutral0,
        content = {
            when (bottomSheetState) {
                is HomeBottomSheetState.StateLogoutActions -> {
                    LogoutOptionsBottomSheet(
                        onLogout = { dataRetained ->
                            homeViewModel.logout(
                                dataRetained = dataRetained
                            )
                        }
                    )
                }

                else -> {}
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Content(
    homeViewModel: HomeViewModel,
    state: HomeScreenUiState,
    ekaChatViewModel: EkaChatViewModel,
    onClick: (CTA) -> Unit
) {
    val context = LocalContext.current
    val pullRefreshState =
        rememberPullRefreshState(state == HomeScreenUiState.StateLoading, {
            ekaChatViewModel.getUserHash(
                OrbiUserManager.getUserTokenData()?.oid,
                OrbiUserManager.getUserTokenData()?.uuid
            )
            homeViewModel.fetchHome()
            homeViewModel.getUserConfiguration(context.applicationContext)
        })
    if (state == HomeScreenUiState.StateLoading) {
        HomeShimmer()
        return
    }

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
            .background(DarwinTouchPrimaryBgLight)
    ) {
        LazyColumn {
            items(medicalCardsList) {
                HomeCardContent(it, onClick)
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = true,
            state = pullRefreshState,
            contentColor = Purple500,
            scale = true
        )
    }
}

val medicalCardsList = listOf(
    MedicalCard(
        id = "doc_assist",
        iconResId = R.drawable.ic_ai_chat_custom,
        title = "Chat with DocAssist AI",
        description = "Ask DocAssist AI any medical question about your practice or patients.",
        buttonText = "Chat now",
        clickAction = HomeClick.DOC_ASSIST_CLICK
    ),
    MedicalCard(
        id = "eka_scribe",
        iconResId = R.drawable.ic_bot_audio_custom,
        title = "EkaScribe: AI-Powered Clinical Documentation",
        description = "Use eka scribe to convert voice to coded medical data",
        buttonText = "Try now",
        clickAction = HomeClick.EKA_SCRIBE_CLICK
    ),
    MedicalCard(
        id = "support",
        iconResId = R.drawable.ic_support_custom,
        title = "Need Help? We're On It",
        description = "Start a quick chat with our support team to get the help you need!",
        buttonText = "Chat now",
        clickAction = HomeClick.SUPPORT_CLICK
    )
)
