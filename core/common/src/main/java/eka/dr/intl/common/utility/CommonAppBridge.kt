package eka.dr.intl.common.utility

import android.content.Context
import android.webkit.JavascriptInterface
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class CommonAppBridge(
    private val mContext: Context,
) {
    @OptIn(DelicateCoroutinesApi::class)
    @JavascriptInterface
    fun sendEvent(event: String) {

        GlobalScope.launch {
            val eventJson = JSONObject(event)
            val eventName = eventJson.optString("event")
            val eventInfo = eventJson.optJSONObject("properties") ?: JSONObject()
        }

    }
}