package eka.dr.intl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eka.dr.intl.assistant.navigation.ChatScreenNavModel
import eka.dr.intl.assistant.navigation.ChatTranscriptNavModel
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.UserSharedPref
import eka.dr.intl.common.presentation.component.DoctorStatusComponent
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.navigation.BottomNavBar
import eka.dr.intl.navigation.NavigationScreen
import eka.dr.intl.navigation.isRouteInHierarchy
import eka.dr.intl.patients.naviagtion.AddPatientToDirectoryNavModel
import eka.dr.intl.patients.naviagtion.MedicalRecordsNavModel
import eka.dr.intl.patients.naviagtion.PatientActionsScreenNavModel
import eka.dr.intl.presentation.activity.WebViewLoginActivity
import eka.dr.intl.presentation.components.HomeDrawer
import eka.dr.intl.presentation.screens.DoctorAppSettingUpScreen
import eka.dr.intl.presentation.states.HomeBottomSheetState
import eka.dr.intl.presentation.states.HomeScreenUiState
import eka.dr.intl.presentation.viewModel.DrawerViewModel
import eka.dr.intl.presentation.viewModel.HomeViewModel
import eka.dr.intl.ui.theme.EkaCareIntlTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {
    private lateinit var scope: CoroutineScope
    private lateinit var snackbarHostState: SnackbarHostState
    private lateinit var navController: NavHostController
    private val homeViewModel by viewModels<HomeViewModel>()
    private val drawerViewModel by viewModels<DrawerViewModel>()
    private val doctorStatusViewModel by viewModels<DoctorStatusViewModel>()
    private val ekaChatViewModel by viewModels<EkaChatViewModel>()

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                OrbiUserManager.getUserTokenData()
                refreshAll()
                displayMainContent()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // drawer viewmodel ka dekhna h
        window.statusBarColor = Color.Transparent.value.toInt()
        if (OrbiUserManager.isLoggedin(this)) {
            displayMainContent()
        } else {
            Intent(this, WebViewLoginActivity::class.java).also {
                loginLauncher.launch(it)
            }
        }
    }

    private fun displayMainContent() {
        init()
        setContent {
            scope = rememberCoroutineScope()
            navController = rememberNavController()
            snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current
            val state by homeViewModel.homeScreenUiState.collectAsState()
            val patientDirectorySyncStatus =
                homeViewModel.patientDirectorySyncStatus.collectAsState()
            LaunchedEffect(patientDirectorySyncStatus.value.error) {
                if (!patientDirectorySyncStatus.value.loading && !patientDirectorySyncStatus.value.error.isNullOrEmpty()) {
                    scope.launch(Dispatchers.IO) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Failed to sync patient directory",
                            actionLabel = "Retry",
                            duration = SnackbarDuration.Indefinite
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                homeViewModel.getUserConfiguration(context.applicationContext)
                            }

                            SnackbarResult.Dismissed -> {
                                /* Handle snackbar dismissed */
                            }
                        }
                    }
                }
            }
            EkaCareIntlTheme {
                DoctorAppSettingUpScreen(showDialog = patientDirectorySyncStatus.value.loading || state is HomeScreenUiState.StateLoading)
                SuccessScreen()
            }
        }
    }

    private fun init() {
        homeViewModel.syncAddAndUpdatePatientFromLocal()
        refreshAll()
    }

    @Composable
    private fun SuccessScreen() {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = DarwinTouchNeutral0,
                    content = {
                        HomeDrawer(
                            drawerViewModel = drawerViewModel,
                            onLogout = {
                                scope.launch { drawerState.close() }
                                homeViewModel.homeBottomSheetState.value =
                                    HomeBottomSheetState.StateLogoutActions
                            },
                            onSwitch = {
                                UserSharedPref.invoke(context = this@MainActivity)
                                    .saveUserToken(
                                        sessionToken = it.session,
                                        refreshToken = it.refresh
                                    )
                                OrbiUserManager.saveSelectedBusiness(it.bId)
                                scope.launch { drawerState.close() }
                                refreshAll()
                            }
                        )
                    }
                )
            },
            content = {
                ScaffoldWrapper(
                    drawerState = drawerState,
                )
            }
        )
    }

    @Composable
    fun ScaffoldWrapper(
        drawerState: DrawerState,
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination
        val noBottomBarRoutes = remember {
            listOf(
                MedicalRecordsNavModel::class,
                PatientActionsScreenNavModel::class,
                AddPatientToDirectoryNavModel::class,
                ChatScreenNavModel::class,
                ChatTranscriptNavModel::class
            )
        }
        val showBottomBar = remember(currentRoute) {
            noBottomBarRoutes.none { currentRoute?.isRouteInHierarchy(it) == true }
        }
        val navigationScreen = remember {
            @Composable {
                NavigationScreen(
                    navController = navController,
                    drawerState = drawerState,
                )
            }
        }

        val paddingColor = remember(currentRoute) {
            BackgroundUtils.getPaddingColor(currentRoute)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            DoctorStatusComponent(
                viewModel = doctorStatusViewModel,
                backgroundStatus = paddingColor
            )
            Box(modifier = Modifier.weight(1f)) {
                navigationScreen()

                if (showBottomBar) {
                    BottomNavBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        navController = navController
                    )
                }
            }
        }
    }

    private fun refreshAll() {
        homeViewModel.fetchHome()
        homeViewModel.updateUserTokenData()
        homeViewModel.getUserConfiguration(this.applicationContext)
        ekaChatViewModel.getUserHash(
            OrbiUserManager.getUserTokenData()?.oid,
            OrbiUserManager.getUserTokenData()?.uuid
        )
        ekaChatViewModel.getChatSessions(null)
    }
}
