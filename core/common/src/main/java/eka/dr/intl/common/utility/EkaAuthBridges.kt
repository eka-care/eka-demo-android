package eka.dr.intl.common.utility

import android.app.Activity
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.google.gson.Gson
import eka.dr.intl.common.UserSharedPref
import eka.dr.intl.common.data.dto.response.VerifyOTPEmrResponseV1
import org.json.JSONObject

class EkaAuthBridges(private val mContext: Context, private val onLoginSuccess: () -> Unit) {

    @JavascriptInterface
    fun onAuthSuccess(params: String) {
        (mContext as? Activity)?.runOnUiThread {
            try {
                val data = Gson().fromJson(params, VerifyOTPEmrResponseV1::class.java).response.data
                Log.d("AppBridge", "onAuthSuccess: $data")
                UserSharedPref(mContext).saveUserToken(
                    data.tokens.sess,
                    data.tokens.refresh
                )
                onLoginSuccess()
            } catch (e: Exception) {
                Log.e("AppBridge", "Error processing auth success: ${e.message}")
            }
        }
    }

    @JavascriptInterface
    fun onAuthError(params: String) {
        (mContext as? Activity)?.runOnUiThread {
            try {
                val jsonObject = JSONObject(params)
                val errorMessage = jsonObject.optString("message", "Something Went Wrong")
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(mContext, "Error parsing error response", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JavascriptInterface
    fun onAuthClose() {
        (mContext as? Activity)?.runOnUiThread {
            (mContext as? Activity)?.finish()
        }
    }
}
