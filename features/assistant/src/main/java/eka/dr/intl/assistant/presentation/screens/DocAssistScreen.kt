package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.common.data.dto.response.ChatContext

@Composable
fun DocAssistScreen (
    openDrawer: () -> Unit = {},
    viewModel: EkaChatViewModel,
    onPatientChatClick: (ChatContext) -> Unit,
    navigateToChatScreen: (String) -> Unit,
    onEmptyScreen: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Doc Assist")
    }
}