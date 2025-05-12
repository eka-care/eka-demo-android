package eka.dr.intl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.eka.voice2rx_sdk.common.models.VoiceError
import com.eka.voice2rx_sdk.data.local.models.ContextData
import com.eka.voice2rx_sdk.data.local.models.Doctor
import com.eka.voice2rx_sdk.data.local.models.Name
import com.eka.voice2rx_sdk.data.local.models.Patient
import com.eka.voice2rx_sdk.data.local.models.Personal
import com.eka.voice2rx_sdk.data.local.models.Profile
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration
import com.eka.voice2rx_sdk.sdkinit.Voice2Rx
import com.eka.voice2rx_sdk.sdkinit.Voice2RxInitConfig
import com.google.gson.Gson
import com.konovalov.vad.silero.config.FrameSize
import com.konovalov.vad.silero.config.SampleRate
import eka.care.documents.Document
import eka.care.documents.DocumentConfiguration
import eka.dr.intl.assistant.di.getChatModules
import eka.dr.intl.common.BaseApp
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.LoginLifeCycleCallback
import eka.dr.intl.common.OrbiPrefHelper
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.V2RxUtils
import eka.dr.intl.common.data.dto.response.UserTokenData
import eka.dr.intl.common.listeners.Voice2RxSessionListener
import org.koin.core.qualifier.named
import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.di.getDatabaseModule
import eka.dr.intl.di.ExploreEkaKoin
import eka.dr.intl.di.getExploreEkaModules
import eka.dr.intl.domain.usecase.AddWorkspaceUseCase
import eka.dr.intl.network.Networking
import eka.dr.intl.patients.di.getPatientDirectoryModules
import eka.dr.intl.utility.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import java.lang.ref.WeakReference
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class EkaApp : BaseApp(), IAmCommon, LoginLifeCycleCallback, KoinComponent {
    companion object {
        lateinit var instance: EkaApp
        const val TAG = "OrbiApp"
        const val UPDATED_FCM_TOKEN = "updatedFcm"
        private const val SHARED_PREF_FILE_NAME = "orbi_pref"
        const val APP_VERSION_KEY = "app_version"

        private var voice2RxSessionListener: Voice2RxSessionListener? = null
        private val acl: HashSet<Restrictions> = hashSetOf()
        fun <T> setValue(key: String, value: T?) {
            OrbiPrefHelper.setValue(instance.getAppPreference(), key, value)
        }

        fun <T> getValue(key: String, default: T?): T? {
            return OrbiPrefHelper.getValue(instance.getAppPreference(), key, default)
        }
    }

    var sessionData: HashMap<String, String?> = HashMap()
    private val appSharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
    }
    private var currentActivity: WeakReference<Activity>? = null

    internal fun setCurrentActivity(mCurrentActivity: Activity?) {
        this.currentActivity = WeakReference(mCurrentActivity)
    }

    fun getAppPreference(): SharedPreferences {
        return appSharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoinAndAddModules(instance)
        OrbiUserManager.init(this)
        Networking.init(BuildConfig.EKA_CARE_URL, OkHttpSetup())
        Document.init(
            context = this,
            documentConfiguration = DocumentConfiguration(
                okHttpSetup = NewOkHttpSetup(),
                host = BuildConfig.RECORDS_URL,
                vitalsEnabled = false
            )
        )
    }

    override fun navigateTo(context: Activity, pageId: String, params: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun getNavigationIntent(
        context: Context,
        pageId: String,
        params: JSONObject,
        action: (Intent, ArrayList<Intent>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun <T> setValue(key: String, value: T?) {
        TODO("Not yet implemented")
    }

    override fun <T> setValueCommit(key: String, value: T?): Boolean {
        return OrbiPrefHelper.setValueCommit(getAppPreference(), key, value)
    }

    private fun startKoinAndAddModules(context: Context) {
        val modules = getExploreEkaModules() + getDatabaseModule() + getPatientDirectoryModules() + getChatModules()
        startKoin {
            androidLogger()
            androidContext(context)
            modules(modules)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun storeAuthTokens(sessionToken: String, refreshToken: String) {
        if (sessionToken.isEmpty() || refreshToken.isEmpty()) return

        val addWorkspaceUseCase: AddWorkspaceUseCase by inject(
            qualifier = named(ExploreEkaKoin.ADD_WORKSPACE_USE_CASE)
        )

        try {
            // Use Kotlin Base64 decoder with optional padding (suitable for JWT)
            val base64 = Base64.Default.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
            val jwtPayload = sessionToken.split(".")[1]
            val decoded = base64.decode(jwtPayload)
            val decodedPayload = String(decoded, StandardCharsets.UTF_8)

            // Parse the decoded payload into your data class
            val userTokenData = Gson().fromJson(decodedPayload, UserTokenData::class.java)

            val businessEntity = BusinessEntity(
                bId = userTokenData.businessId ?: "b-${userTokenData?.oid}",
                session = sessionToken,
                refresh = refreshToken,
                name = if (userTokenData.salutation.isNullOrEmpty()) "${userTokenData.name}" else "${userTokenData.salutation} ${userTokenData.name}",
                oid = userTokenData.oid
            )

            // Save the business entity using the injected use case
            CoroutineScope(Dispatchers.IO).launch {
                addWorkspaceUseCase.invoke(businessEntity)
            }
        } catch (e: Exception) {
            Log.e("AuthStore", "Error decoding and storing token: ${e.message}", e)
        }
    }


    override fun <T> getValue(key: String, default: T?): T? {
        return OrbiPrefHelper.getValue(getAppPreference(), key, default)
    }

    override fun getCommonHeaders(): HashMap<String, String> {
        return Utility.getBasicHeaders()
    }

    override fun selectedLanguage(): String {
        TODO("Not yet implemented")
    }

    override fun sendEvent(eventName: String, param: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun sendPageViewEvent(pageName: String, param: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun sendWebengageEvent(eventName: String, params: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun sendBranchEvent(eventName: String, params: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun getSessionUtm(): HashMap<String, String?> {
        TODO("Not yet implemented")
    }

    override fun ifUpdateRequired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserOid(): String? {
        TODO("Not yet implemented")
    }

    override fun getUserUuid(): String? {
        TODO("Not yet implemented")
    }

    override fun getUserGender(): String? {
        TODO("Not yet implemented")
    }

    override fun getUserDOB(): String? {
        TODO("Not yet implemented")
    }

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    override fun isLoggedIn(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAllowedToAccess(permission: Restrictions): Boolean {
        return !acl.contains(permission)
    }

    override fun saveAclData(aclData: List<Restrictions>) {
        TODO("Not yet implemented")
    }

    override fun onVoice2RxSessionCompleted(mode: Voice2RxType) {
        Voice2Rx.stopVoice2Rx()
    }

    override fun clearPref() {
        OrbiPrefHelper.clearPref(getAppPreference())
    }

    override fun stopVoice2RxSession() {
        voice2RxSessionListener?.stopVoice2RxSession()
    }

    override fun pauseVoice2RxSession() {
        Voice2Rx.pauseVoice2Rx()
    }

    override fun resumeVoice2RxSession() {
        Voice2Rx.resumeVoice2Rx()
    }

    override fun setSessionListener(listener: Voice2RxSessionListener) {
        if (voice2RxSessionListener == null) {
            voice2RxSessionListener = listener
        }
    }

    override fun initializeVoice2Rx(
        sessionId: String,
        context: Context,
        s3Config: AwsS3Configuration?,
        patientId: String?,
        visitId: String,
        onStop: (String, Int) -> Unit,
        onError: (String) -> Unit
    ) {
        Voice2Rx.init(
            context = context,
            config = Voice2RxInitConfig(
                onStart = { session ->

                },
                onStop = { session, recordedFiles ->
                    Log.d(TAG, "Recording Stop.")
                    voice2RxSessionListener?.onStopVoice2RxSession(
                        sessionInfo = session,
                        recordedFiles = recordedFiles
                    )
                    onStop(session, recordedFiles)
                },
                onError = { session, error ->
                    if (error == VoiceError.UNKNOWN_ERROR) {
                        voice2RxSessionListener?.onError(error)
                    } else if (error == VoiceError.MICROPHONE_PERMISSION_NOT_GRANTED) {
                        voice2RxSessionListener?.onError(error)
                    }
                },
                onPaused = { session ->
                    voice2RxSessionListener?.pauseVoice2RxSession()
                },
                onResumed = { session ->
                    voice2RxSessionListener?.resumeVoice2RxSession()
                },
                docOid = OrbiUserManager.getUserTokenData()?.oid ?: "",
                docUuid = OrbiUserManager.getUserTokenData()?.uuid ?: "",
                contextData = ContextData(
                    doctor = Doctor(
                        id = OrbiUserManager.getUserTokenData()?.oid ?: "",
                        profile = Profile(
                            personal = Personal(
                                name = Name(
                                    f = OrbiUserManager.getUserTokenData()?.name ?: "",
                                    l = ""
                                )
                            )
                        )
                    ),
                    patient = Patient(id = patientId),
                    visitid = visitId
                ),
                maxCutDuration = 25,
                prefCutDuration = 10,
                despCutDuration = 20,
                sampleRate = SampleRate.SAMPLE_RATE_16K,
                frameSize = FrameSize.FRAME_SIZE_512,
                callerId = "",
                ownerId = V2RxUtils.getOwnerId(),
                okHttpSetup = NewOkHttpSetup()
            )
        )
    }
    override fun startVoiceConversation(sessionId: String, mode: Voice2RxType) {
        Voice2Rx.startVoice2Rx(mode = mode, session = sessionId)
        voice2RxSessionListener?.onStartVoice2RxSession(
            sessionInfo = sessionId,
            onError = { error ->

            }
        )
    }

    override fun retrySession(
        context: Context,
        sessionId: String,
        s3Config: AwsS3Configuration,
        messageJson: String
    ) {
        voice2RxSessionListener?.retrySession(
            context = context,
            sessionId = sessionId,
            s3Config = s3Config,
            messageJson = messageJson
        )
    }

    override fun onLoginSuccess(isNewUser: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onLoginCancelled() {
        TODO("Not yet implemented")
    }

    override fun onLogOut() {
        TODO("Not yet implemented")
    }

    override fun onLoginFailed(errorMsg: String?) {
        TODO("Not yet implemented")
    }
}