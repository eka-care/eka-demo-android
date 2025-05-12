package eka.dr.intl.common

import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference

class UserSharedPref private constructor(
    val context: Context, sharedPrefName: String,
) : SecuredSharedPref(context, sharedPrefName) {

    companion object {
        private const val SESSION_TOKEN = "session_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val SESSION_COUNT = "SESSION_COUNT"
        const val HOME_DATA_KEY = "home_data"
        private const val LOGGED_IN_USER_ROLE = "logged_in_user_role"
        private const val SELECTED_BUSINESS_ID = "selected_business_id"
        private const val SELECTED_DOCTOR_ID = "selected_doctor_id"
        private const val CHAT_USER_HASH = "chat_user_hash"

        private var instance: UserSharedPref? = null
        private var contextRef: WeakReference<Context>? = null

        operator fun invoke(context: Context): UserSharedPref = synchronized(this) {
            if (instance == null) {
                contextRef = WeakReference(context.applicationContext)
                instance = UserSharedPref(contextRef!!.get()!!, "userSharedPref_intl")
            }
            instance as UserSharedPref
        }
    }

    fun <T> getValue(key: String, default: T?): T? {
        return instance?.let {
            OrbiPrefHelper.getValue(it, key, default)
        }
    }

    fun <T> setValue(k: String, value: T?) {
        OrbiPrefHelper.setValue(instance!!, k, value)
    }

    fun <T> setValueCommit(k: String, value: T?) {
        OrbiPrefHelper.setValueCommit(instance!!, k, value)
    }

    fun remove(k: String?) {
        val editor: SharedPreferences.Editor = instance!!.edit()
        editor.remove(k)
        editor.commit()
    }

    private fun removeAll(): Boolean {
        val editor: SharedPreferences.Editor = instance!!.edit()
        editor.clear()
        return editor.commit()
    }

    fun saveUserToken(sessionToken: String, refreshToken: String) {
        setValueCommit(SESSION_TOKEN, sessionToken)
        setValueCommit(REFRESH_TOKEN, refreshToken)
        (context.applicationContext as IAmCommon).storeAuthTokens(
            sessionToken = sessionToken,
            refreshToken = refreshToken
        )
    }

    fun isUserLoggedIn(): Boolean {
        val sessionToken = getValue(SESSION_TOKEN, "") ?: ""
        val refreshToken = getValue(REFRESH_TOKEN, "") ?: ""

        return sessionToken.isNotEmpty() && refreshToken.isNotEmpty()
    }

    fun getSessionToken(): String? {
        return getValue<String?>(SESSION_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return getValue<String?>(REFRESH_TOKEN, null)
    }

    fun saveChatUserHash(userHash: String, oid: String) {
        setPrefValueCommit(getUserHashKey(oid), userHash)
        setValueCommit(getUserHashKey(oid), userHash)
    }

    fun getUserHashKey(oid: String): String {
        return CHAT_USER_HASH + "_" + oid
    }

    fun getChatUserHash(oid: String): String? {
        val hash = getPrefValue(getUserHashKey(oid), "")
        return hash
    }

    fun logoutUser() {
        instance!!.removeAll()
        instance = null
        (context as IAmCommon).clearPref()
        OrbiUserManager.logout()
    }

    fun updateSession() {
        val count = getPrefValue(SESSION_COUNT, 0) ?: 0
        setPrefValue(SESSION_COUNT, count + 1)
    }

    fun getSessionCount(): Int {
        return getPrefValue(SESSION_COUNT, 1) ?: 1
    }

    fun <T> getPrefValue(key: String, default: T?): T? {
        return (contextRef?.get() as IAmCommon).getValue(key = key, default = default)
    }

    private fun <T> setPrefValue(k: String, value: T?) {
        return (contextRef?.get() as IAmCommon).setValue(key = k, value = value)
    }

    private fun <T> setPrefValueCommit(k: String, value: T?) {
        (contextRef?.get() as IAmCommon).setValueCommit(key = k, value = value)
    }

    fun saveRole(clinicId: String) {
        OrbiPrefHelper.setValue(instance!!, LOGGED_IN_USER_ROLE, clinicId)
    }

    fun getRole(): String? {
        val value = OrbiPrefHelper.getValue(instance!!, LOGGED_IN_USER_ROLE, "") as String
        return value.ifEmpty {
            null
        }
    }

    fun saveBusiness(businessId: String) {
        OrbiPrefHelper.setValue(instance!!, SELECTED_BUSINESS_ID, businessId)
    }

    fun saveSelectedDoctorId(doctorId: String?) {
        OrbiPrefHelper.setValue(instance!!, SELECTED_DOCTOR_ID, doctorId)
    }

    fun getSelectedDoctorId(): String? {
        val value = OrbiPrefHelper.getValue(instance!!, SELECTED_DOCTOR_ID, "") as String
        return value.ifEmpty {
            null
        }
    }

    fun getBusiness(): String? {
        val value = OrbiPrefHelper.getValue(instance!!, SELECTED_BUSINESS_ID, "") as String
        return value.ifEmpty {
            null
        }
    }
}