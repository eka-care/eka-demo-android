package eka.dr.intl.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType
import org.json.JSONObject
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration
import eka.dr.intl.common.listeners.Voice2RxSessionListener

interface IAmCommon {
    fun navigateTo(context: Activity, pageId: String, params: JSONObject?)
    fun getNavigationIntent(
        context: Context,
        pageId: String,
        params: JSONObject,
        action: (Intent, ArrayList<Intent>) -> Unit
    )

    fun <T> setValue(key: String, value: T?)
    fun <T> setValueCommit(key: String, value: T?): Boolean
    fun storeAuthTokens(sessionToken: String, refreshToken: String)
    fun <T> getValue(key: String, default: T?): T?
    fun getCommonHeaders(): HashMap<String, String>
    fun selectedLanguage(): String
    fun sendEvent(eventName: String, param: JSONObject? = null)
    fun sendPageViewEvent(pageName: String, param: JSONObject? = null)
    fun sendWebengageEvent(eventName: String, params: JSONObject? = null)
    fun sendBranchEvent(eventName: String, params: JSONObject? = null)
    fun getSessionUtm(): HashMap<String, String?>
    fun ifUpdateRequired(): Boolean
    fun getUserOid(): String?
    fun getUserUuid(): String?
    fun getUserGender(): String?
    fun getUserDOB(): String?
    fun getUserName(): String?
    fun isLoggedIn(context: Context): Boolean
    fun isAllowedToAccess(permission: Restrictions): Boolean
    fun saveAclData(aclData: List<Restrictions>)
    fun onVoice2RxSessionCompleted(
        mode: Voice2RxType
    )
    fun clearPref()
    fun setSessionListener(listener: Voice2RxSessionListener)

    fun stopVoice2RxSession()
    fun pauseVoice2RxSession()
    fun resumeVoice2RxSession()
    fun initializeVoice2Rx(
        sessionId: String,
        context: Context,
        s3Config: AwsS3Configuration?,
        patientId: String?,
        visitId: String,
        onStop: (String, Int) -> Unit,
        onError: (String) -> Unit,
    )

    fun startVoiceConversation(
        sessionId: String,
        mode: Voice2RxType,
    )

    fun retrySession(
        context: Context,
        sessionId: String,
        s3Config: AwsS3Configuration,
        messageJson: String,
    )
}