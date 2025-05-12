package eka.dr.intl

import android.widget.Toast
import eka.dr.intl.common.UserSharedPref
import eka.dr.intl.data.remote.api.UserServiceEmr
import eka.dr.intl.data.remote.dto.request.RefreshSessionRequest
import eka.dr.intl.network.IOkHttpSetup
import eka.dr.intl.network.Networking
import eka.dr.intl.utility.Utility
import kotlinx.coroutines.runBlocking
import java.util.Locale

class OkHttpSetup : IOkHttpSetup {
    override fun getDefaultHeaders(url: String): Map<String, String> {
        val headers = Utility.getBasicHeaders()
        headers["device-id"] = Utility.getDeviceId(EkaApp.instance)
        headers["locale"] = Locale.getDefault().language
        return headers
    }

    override fun refreshAuthToken(url: String): Map<String, String>? {
        return runBlocking {
            val sessionToken = refreshToken()
            if (sessionToken.isNullOrBlank()) {
                null
            } else {
                getDefaultHeaders(url)
            }
        }
    }

    override fun onSessionExpire() {

        Toast.makeText(
            EkaApp.instance,
            EkaApp.instance.getString(R.string.logout_mesaage),
            Toast.LENGTH_LONG
        ).show()
        UserSharedPref(EkaApp.instance).logoutUser()
//        val intent = Intent(EkaApp.instance, SplashActivity::class.java)
//        intent.flags =
//            Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        EkaApp.instance.applicationContext.startActivity(intent)
    }

    private suspend fun refreshToken(): String? {
        var sessionToken = UserSharedPref(EkaApp.instance).getSessionToken()
        val refreshToken = UserSharedPref(EkaApp.instance).getRefreshToken()
        if (sessionToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            return null
        }
        val request = RefreshSessionRequest(
            refresh = refreshToken,
            sessionToken = sessionToken
        )

        val userService: UserServiceEmr =
            Networking.create(UserServiceEmr::class.java, BuildConfig.EKA_CARE_AUTH_GO_URL)
        val response = userService.refresh(request)

        if (response.isSuccessful) {
            val refreshResponse = response.body()
            refreshResponse?.let {
                sessionToken = it.sessionToken
                UserSharedPref(EkaApp.instance).saveUserToken(
                    it.sessionToken,
                    it.refresh
                )
            }
        }

        return sessionToken

    }
}