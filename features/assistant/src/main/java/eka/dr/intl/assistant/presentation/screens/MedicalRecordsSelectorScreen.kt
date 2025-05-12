package eka.dr.intl.assistant.presentation.screens

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import eka.care.documents.ui.presentation.model.RecordModel
import eka.care.documents.ui.presentation.screens.DocumentScreen
import eka.care.documents.ui.presentation.screens.Mode
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.patients.data.local.entity.PatientEntity

@Composable
fun MedicalRecordsSelectorScreen(
    patientEntity: PatientEntity?,
    chatContext: ChatContext?,
    onClick: (CTA) -> Unit,
    selectedRecords: (List<RecordModel>) -> Unit
) {
    val doctorId = OrbiUserManager.getSelectedDoctorId()
    val user = OrbiUserManager.getUserTokenData()
    val params = JsonObject().apply {
        addProperty("filter_id", chatContext?.patientId ?: "")
        addProperty("owner_id", doctorId ?: "")
        addProperty("links", patientEntity?.links?.joinToString(",") ?: "")
        addProperty("p_uuid", patientEntity?.uuid ?: "")
        addProperty("name", chatContext?.patientName ?: user?.name ?: "")
        addProperty("age", patientEntity?.age ?: -1)
        addProperty("gen", patientEntity?.gender?.value ?: "")
    }
    DocumentScreen(param = params, onBackClick = {
        onClick(CTA(action = ActionType.ON_BACK.stringValue))
    }, mode = Mode.SELECTION,
        selectedRecords = {
        selectedRecords(it)
    })
}