package eka.dr.intl.patients.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import eka.care.documents.ui.presentation.screens.DocumentScreen
import eka.care.documents.ui.presentation.screens.Mode
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.patients.naviagtion.MedicalRecordsNavModel

@Composable
fun DocumentScreenComponent(
    navData: MedicalRecordsNavModel,
    isUploadedEnabled: Boolean,
    onBackClick: () -> Unit,
) {
    val params = buildDocumentScreenParams(navData)
    Scaffold(
        containerColor = DarwinTouchNeutral0,
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.White))
        },
        content = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(it)) {
                DocumentScreen(
                    param = params,
                    isUploadEnabled = isUploadedEnabled,
                    onBackClick = onBackClick,
                    mode = Mode.VIEW,
                )
            }
        }
    )
}

private fun buildDocumentScreenParams(navData: MedicalRecordsNavModel): JsonObject {
    val params = JsonObject()
    params.addProperty("filter_id", navData.filterId)
    params.addProperty("owner_id", navData.ownerId)
    params.addProperty("name", navData.name)
    params.addProperty("gen", navData.gen)
    params.addProperty("age", navData.age)
    params.addProperty("links", navData.links)
    return params
}