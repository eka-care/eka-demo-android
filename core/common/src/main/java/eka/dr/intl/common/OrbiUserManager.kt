package eka.dr.intl.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import eka.dr.intl.common.UserSharedPref.Companion.HOME_DATA_KEY
import eka.dr.intl.common.data.dto.response.Profile
import eka.dr.intl.common.data.dto.response.UserTokenData
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object OrbiUserManager {
    private var callbacks: ArrayList<LoginLifeCycleCallback> = ArrayList()
    private var currentLoginCallback: LoginLifeCycleCallback? = null
    private var userData: Profile? = null
    private lateinit var mContext: Application

    fun init(context: Application) {
        mContext = context
    }

    fun registerLoginLifeCycleCallbacks(loginLifeCycleCallback: LoginLifeCycleCallback) {
        callbacks.add(loginLifeCycleCallback)
    }

    //TODO add login source param in below method to identify login source in login flow
    fun requestLogin(
        context: Activity,
        nextActivity: Activity,
        loginLifeCycleCallback: LoginLifeCycleCallback?,
        pid: String? = null,
    ) {
        if (!UserSharedPref(context.application).isUserLoggedIn()) {
            loginLifeCycleCallback?.let { currentLoginCallback = loginLifeCycleCallback }
            val intent = Intent(context, nextActivity::class.java)
            pid?.let {
                intent.putExtra("pid", pid)
            }
            context.startActivity(intent)
        } else {
            loginLifeCycleCallback?.onLoginSuccess(false)
        }
    }

    fun isLoggedin(context: Context): Boolean {
        return UserSharedPref(context.applicationContext).isUserLoggedIn()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getUserTokenData(): UserTokenData? {
        val sessToken = UserSharedPref(mContext).getSessionToken() ?: return null

        try {
            val base64 = Base64.Default.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
            val jwtPayload = sessToken.split(".")[1]
            val decoded = base64.decode(jwtPayload)
            val userData = String(decoded, StandardCharsets.UTF_8)
            JsonParser.parseString(userData).asJsonObject
            return Gson().fromJson(userData, UserTokenData::class.java)
        } catch (e: Exception) {
            Log.e("getUserTokenData", e.message.toString())
        }

        return null
    }

    fun saveChatUserHash(userHash: String, oid: String) {
        UserSharedPref(mContext).saveChatUserHash(userHash, oid)
    }

    fun getChatUserHash(oid: String): String? {
        return UserSharedPref(mContext).getChatUserHash(oid)
    }

    fun saveUserRole(role: String) {
        UserSharedPref(mContext).saveRole(role)
    }

    fun getUserRole(): String? {
        return UserSharedPref(mContext).getRole()
    }

    fun saveSelectedBusiness(businessId: String) {
        UserSharedPref(mContext).saveBusiness(businessId)
    }

    fun saveSelectedDoctorId(doctorId: String?) {
        UserSharedPref(mContext).saveSelectedDoctorId(doctorId)
    }

    fun getSelectedDoctorId(): String? {
        return UserSharedPref(mContext).getSelectedDoctorId()
    }


    fun getSelectedBusiness(): String? {
        return UserSharedPref(mContext).getBusiness()
    }

    fun saveHomeData(data: String) {
        UserSharedPref(mContext).setValue(HOME_DATA_KEY, data)
    }

    fun getHomeData(): String? {
        return UserSharedPref(mContext).getValue(HOME_DATA_KEY, null)
    }


    fun loginSuccess(isNewUser: Boolean = false) {
        try {
            currentLoginCallback?.let {
                it.onLoginSuccess(isNewUser)
                currentLoginCallback = null
            }
            for (callback in callbacks) {
                callback.onLoginSuccess(isNewUser)
            }
        } catch (e: Exception) {
            Log.e("loginSuccess", e.message.toString())
        }
    }


    internal fun logout() {
        userData = null
        currentLoginCallback?.let {
            it.onLogOut()
            currentLoginCallback = null
        }
        for (callback in callbacks) {
            callback.onLogOut()
        }
    }

}

interface LoginLifeCycleCallback {
    fun onLoginSuccess(isNewUser: Boolean)
    fun onLoginCancelled()
    fun onLogOut()
    fun onLoginFailed(errorMsg: String?)
}