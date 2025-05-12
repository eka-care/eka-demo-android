package eka.dr.intl.navigation

import android.content.Intent
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.google.gson.Gson
import eka.dr.intl.BuildConfig
import eka.dr.intl.WebViewActivity
import eka.dr.intl.assistant.navigation.AssistantNavModel
import eka.dr.intl.assistant.navigation.ChatBotPatientSessionNavModel
import eka.dr.intl.assistant.navigation.ChatScreenNavModel
import eka.dr.intl.assistant.navigation.ChatTranscriptNavModel
import eka.dr.intl.assistant.navigation.navigateToChatBotPatientSession
import eka.dr.intl.assistant.navigation.navigateToChatScreen
import eka.dr.intl.assistant.navigation.navigateToChatTranscript
import eka.dr.intl.assistant.navigation.navigateToDocAssist
import eka.dr.intl.assistant.presentation.screens.EkaBotPatientDetailScreen
import eka.dr.intl.assistant.presentation.screens.EkaChatBotDetailScreen
import eka.dr.intl.assistant.presentation.screens.EkaChatBotMainScreen
import eka.dr.intl.assistant.presentation.screens.EkaTranscriptViewerScreen
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.presentation.viewmodel.Voice2RxViewModel
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.PageParams
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.patients.naviagtion.AddPatientToDirectoryNavModel
import eka.dr.intl.patients.naviagtion.MedicalRecordsNavModel
import eka.dr.intl.patients.naviagtion.PatientActionsScreenNavModel
import eka.dr.intl.patients.naviagtion.PatientNavModel
import eka.dr.intl.patients.naviagtion.navigateToAddPatientToDirectory
import eka.dr.intl.patients.naviagtion.navigateToMedicalRecords
import eka.dr.intl.patients.naviagtion.navigateToPatientActionsScreen
import eka.dr.intl.patients.naviagtion.navigateToPatientDirectory
import eka.dr.intl.patients.presentation.components.PatientActionsScreen
import eka.dr.intl.patients.presentation.screens.AddPatientToDirectoryScreen
import eka.dr.intl.patients.presentation.screens.DocumentScreenComponent
import eka.dr.intl.patients.presentation.screens.PatientScreen
import eka.dr.intl.presentation.screens.HomeClick
import eka.dr.intl.presentation.screens.HomeScreen
import eka.dr.intl.presentation.viewModel.HomeViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

@Composable
fun NavigationScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    startDestination: KClass<*> = HomeNavModel::class
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDocAssistAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.DOC_ASSIST)
    val isMedicalRecordsAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.UPLOAD_MEDICAL_RECORDS)
    val ekaChatViewModel = koinViewModel<EkaChatViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val voice2RxViewModel = koinViewModel<Voice2RxViewModel>()
    val doctorStatusViewModel = koinViewModel<DoctorStatusViewModel>()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<HomeNavModel> {
            HomeScreen(
                homeViewModel = homeViewModel,
                drawerState = drawerState,
                ekaChatViewModel = ekaChatViewModel,
                onClick = {
                    when (it.action) {
                        HomeClick.SEARCH_CLICK.value -> navController.navigateToPatientDirectory()
                        HomeClick.DOC_ASSIST_CLICK.value -> {
                            if(isDocAssistAllowed) {
                                navController.navigateToDocAssist()
                            }
                        }
                        HomeClick.EKA_SCRIBE_CLICK.value -> {
                            navController.navigateToChatScreen(
                                sessionId = ChatUtils.getNewSessionId()
                            )
                        }
                        HomeClick.SUPPORT_CLICK.value -> {
                            val params = JSONObject()
                            val url = "${BuildConfig.MDR_URL}app/my-support/tickets"
                            params.put("url", url)
                            params.put("toolbar", false)
                            params.put("title", "Support Tickets")
                            params.put("requiredCache", url)
                            val intent = Intent(context, WebViewActivity::class.java)
                            context.startActivity(intent)
                        }

                    }
                },
            )
        }
        composable<PatientNavModel> {
            val data = it.toRoute<PatientNavModel>()
            PatientScreen(
                navData = data,
                drawerState = drawerState,
                openMedicalRecords = { filterId, ownerId, links, name, gen, age ->
                    navController.navigateToMedicalRecords(
                        filterId = filterId,
                        ownerId = ownerId,
                        links = links,
                        name = name,
                        gen = gen,
                        age = age
                    )
                },
                openAddPatientNavigation = { pid, from, phone, email, name ->
                    navController.navigateToAddPatientToDirectory(
                        pid = pid,
                        from = from,
                        phone = phone,
                        email = email,
                        name = name
                    )
                },
                openPatientActionsScreen = { pid ->
                    navController.navigateToPatientActionsScreen(pid = pid)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        if (isDocAssistAllowed) {
            composable<AssistantNavModel> {
                EkaChatBotDetailScreen(
                    viewModel = ekaChatViewModel,
                    openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onPatientChatClick = { chatContext ->
                        navController.navigateToChatBotPatientSession(
                            patientId = chatContext.patientId,
                        )
                    },
                    navigateToChatScreen = { sessionId ->
                        navController.navigateToChatScreen(
                            sessionId = sessionId
                        )
                    },
                    onEmptyScreen = {
                        navController.navigateToChatScreen(
                            sessionId = ChatUtils.getNewSessionId(),
                            navOptions = navOptions {
                                popUpTo(
                                    AssistantNavModel::class
                                ) {
                                    inclusive = true
                                }
                            }
                        )
                    }
                )
            }
        }
        composable<ChatBotPatientSessionNavModel> {
            val data = it.toRoute<ChatBotPatientSessionNavModel>()
            val patientId = data.patientId
            val visitId = data.visitId

            EkaBotPatientDetailScreen(
                navData = data,
                chatViewModel = ekaChatViewModel,
                onClick = { sessionId, chatContext ->
                    if (chatContext == null) {
                        navController.navigateToChatScreen(
                            sessionId = sessionId,
                            patientId = patientId,
                            visitId = visitId,
                        )
                    } else {
                        val chatContextVal = Gson().toJson(chatContext)
                        navController.navigateToChatScreen(
                            sessionId = sessionId,
                            patientId = patientId,
                            visitId = visitId,
                            chatContext = chatContextVal,
                            openType = data.mode
                        )
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onEmptyScreen = {
                    navController.navigateToChatScreen(
                        sessionId = ChatUtils.getNewSessionId(),
                        patientId = patientId,
                        visitId = visitId,
                        navOptions = navOptions {
                            popUpTo(
                                ChatBotPatientSessionNavModel::class
                            ) {
                                inclusive = true
                            }
                        }
                    )
                }
            )
        }
        composable<ChatScreenNavModel> {
            val data = it.toRoute<ChatScreenNavModel>()
            EkaChatBotMainScreen(
                navData = data,
                viewModel = ekaChatViewModel,
                voice2RxViewModel = voice2RxViewModel,
                doctorStatusViewModel = doctorStatusViewModel,
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigateToHome()
                    }
                },
                openTranscriptScreen = { sessionId, transcript ->
                    navController.navigateToChatTranscript(
                        sessionId = sessionId,
                        transcript = transcript
                    )
                }
            )
        }
        composable<ChatTranscriptNavModel> {
            val data = it.toRoute<ChatTranscriptNavModel>()
            EkaTranscriptViewerScreen(
                navData = data,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<PatientActionsScreenNavModel> {
            val data = it.toRoute<PatientActionsScreenNavModel>()
            PatientActionsScreen(
                navData = data,
                onClose = {
                    navController.popBackStack()
                },
                openMedicalRecords = { filterId, ownerId, links, name, gen, age ->
                    navController.navigateToMedicalRecords(
                        filterId = filterId,
                        ownerId = ownerId,
                        links = links,
                        name = name,
                        gen = gen,
                        age = age
                    )
                },
                openAddPatientNavigation = { pid, from, phone, email, name ->
                    navController.navigateToAddPatientToDirectory(
                        pid = pid,
                        from = from,
                        phone = phone,
                        email = email,
                        name = name
                    )
                },
                navigateToVoice2Rx = {
                    navController.navigateToChatScreen(
                        sessionId = ChatUtils.getNewSessionId()
                    )
                },
                navigateToDocAssist = {
                    navController.navigateToDocAssist()
                }
            )
        }
        composable<MedicalRecordsNavModel> {
            val data = it.toRoute<MedicalRecordsNavModel>()
            DocumentScreenComponent(
                navData = data,
                isUploadedEnabled = isMedicalRecordsAllowed,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<AddPatientToDirectoryNavModel> {
            val data = it.toRoute<AddPatientToDirectoryNavModel>()
            AddPatientToDirectoryScreen(
                navData = data,
                navigateToPatientDirectory = {
                    navController.navigateToPatientDirectory()
                }
            )
        }
    }
}