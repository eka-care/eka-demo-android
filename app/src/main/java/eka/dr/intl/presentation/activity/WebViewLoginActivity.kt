package eka.dr.intl.presentation.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import eka.dr.intl.common.BuildConfig
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.utility.CommonAppBridge
import eka.dr.intl.common.utility.EkaAuthBridges

class WebViewLoginActivity : ComponentActivity() {
    private lateinit var webView: WebView

    override fun onDestroy() {
        super.onDestroy()
        webView.clearCache(true)
        webView.destroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#FFFFFF")
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        webView = createWebView()
        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        }

        setContent {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    webView
                }
            )
        }
    }

    private fun createWebView(): WebView {
        return WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    evaluateJs(view)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    evaluateJs(view)
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    evaluateJs(view)
                }
            }
            webChromeClient = WebChromeClient()
            addJavascriptInterface(
                EkaAuthBridges(
                    mContext = this@WebViewLoginActivity,
                    onLoginSuccess = {
                        setResult(RESULT_OK)
                        OrbiUserManager.loginSuccess()
                    }
                ),
                "EkaAuth"
            )
            addJavascriptInterface(
                CommonAppBridge(
                    mContext = this@WebViewLoginActivity,
                ),
                "Eka"
            )
            var fileUrl = "file:///android_asset/index.html"
            if (BuildConfig.BUILD_TYPE == "staging") {
                fileUrl = "file:///android_asset/index-dev.html"
            }
            loadUrl(fileUrl)
        }
    }

    fun evaluateJs(view: WebView?) {
        view?.evaluateJavascript(
            "window.onload = function() {\n" +
                    "      // Initialize the app\n" +
                    "      window.initAuthApp({\n" +
                    "        clientId: 'androiddoc',\n" +
                    "        containerId: 'lofe_root',\n" +
                    "        method: '',\n" +
                    "        onSuccess: function (params) {\n" +
                    "          EkaAuth.onAuthSuccess(JSON.stringify(params));\n" +
                    "          EkaAuth.onAuthClose();\n" +
                    "        },\n" +
                    "        onError: function (params) {\n" +
                    "        EkaAuth.onAuthError(JSON.stringify(params));\n" +
                    "        },\n" +
                    "      });\n" +
                    "    };", null
        )
    }
}