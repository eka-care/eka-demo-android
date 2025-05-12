package eka.dr.intl

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import coil.load
import eka.dr.intl.common.BaseActivity
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.PageIdentifier
import eka.dr.intl.common.PageParams
import eka.dr.intl.common.utility.BaseUtil
import eka.dr.intl.data.remote.dto.response.ClinicData
import eka.dr.intl.databinding.LayoutWebviewBinding
import eka.dr.intl.patients.data.local.entity.DoctorEntity
import eka.dr.intl.presentation.viewModel.HomeViewModel
import eka.dr.intl.utility.Utility
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

enum class BottomSheetType {
    DOCTOR,
    CLINIC
}

open class WebViewActivity : BaseActivity() {
    private lateinit var layoutWebViewBinding: LayoutWebviewBinding
    private var webUrl: String? = "https://mdr.eka.care/app/my-support-tickets?flavour=android&Content-Language=en"
    private lateinit var webView: WebView
    private var isFreshLogin = false
    private var currentRetryUrl: String? = ""
    private var showBlockingLoader = false


    private var showToolbar: Boolean = true
    private var pageTitle: String? = null
    private var headerImage: String? = null
    private var shareString: String? = null
    private var showDoctorSelection: Boolean = false
    private var doctor: DoctorEntity? = null
    private var clinic: ClinicData? = null
    private var homeViewModel: HomeViewModel? = null

    var uploadMessage: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDoctorSelection = intent.getBooleanExtra(PageParams.SHOW_DOCTOR_SELECTION, false)
        homeViewModel = HomeViewModel(this.application)
        layoutWebViewBinding = LayoutWebviewBinding.inflate(layoutInflater)
        doctor = OrbiUserManager.getSelectedDoctorId()?.let {
            DoctorEntity(
                id = it,
                pic = "",
                dob = "",
                fln = "",
                email = "",
                gender = "",
                businessId = ""
            )
        }
        val view = layoutWebViewBinding.root
        setContentView(view)
        if (showDoctorSelection) {
            val composeView = layoutWebViewBinding.composeView

            composeView.apply {
                setContent {
                    val listOfDoctors = homeViewModel?.listOfDoctors?.collectAsState()?.value;
                    val bottomSheet = remember { mutableStateOf<BottomSheetType?>(null) }
                    LaunchedEffect(Unit) {
                        homeViewModel?.getListOfDoctors()
                    }

                    LaunchedEffect(listOfDoctors) {
                        if (listOfDoctors?.size == 1) {
                            doctor = listOfDoctors.firstOrNull()
                        }
                    }

                    LaunchedEffect(doctor, clinic) {
                        if (doctor == null && OrbiUserManager.getUserRole() == "STAFF"
                        ) {
                            bottomSheet.value = BottomSheetType.DOCTOR
                        } else if (clinic == null) {
                            bottomSheet.value = BottomSheetType.CLINIC
                        } else {
                            if ( doctor != null) {
                                bottomSheet.value = null
                                initData(doctor?.id, clinic?.id)
                                initUI()
                            }
                        }
                    }
                }
            }
        } else {
            initData()
            initUI()
        }
    }

    private fun initData(docId: String? = null, clinicId: String? = null) {
        if (intent.hasExtra(PageParams.PARAM_URL)) {
            webUrl = "https://mdr.eka.care/app/my-support-tickets?flavour=android&Content-Language=en"
        }
        if (webUrl != null && webUrl!!.contains("ayushman-bharat/minis")) {
            if (clinicId != null) {
                webUrl = BaseUtil.addUrlParam(webUrl!!, "clinic-id", clinicId)
            }
            if (docId != null) {
                webUrl = BaseUtil.addUrlParam(webUrl!!, "doc-id", docId)
            }
        } else {
            if (clinicId != null && webUrl != null) {
                webUrl = BaseUtil.addUrlParam(webUrl!!, "cid", clinicId)
            }
            if (docId != null && webUrl != null) {
                webUrl = BaseUtil.addUrlParam(webUrl!!, "docid", docId)
            }
        }
        pageTitle = intent.getStringExtra("title")
        headerImage = intent.getStringExtra("pic")
        shareString = intent.getStringExtra("share")
        showToolbar = intent.getBooleanExtra("toolbar", false)

        if (showToolbar) {
            setupActionBar()
        }

        if (webUrl.isNullOrEmpty()) {
            finish()
        }
        isFreshLogin = intent.getBooleanExtra("login", false)
        showBlockingLoader = intent.getBooleanExtra("showBlockingLoader", false)

    }


    fun onNavBarInfo(navBarInfo: String) {
        val params = JSONObject(navBarInfo)
        pageTitle = params.getString("title")
        headerImage = params.getString("pic")
        shareString = params.getString("share")
        showToolbar = params.getBoolean("toolbar")

        setupActionBar()
    }

    private fun initUI() {

        if (intent.hasExtra("requiredCache")) {
            EkaApp.instance.setCurrentActivity(this)
            val url = intent.getStringExtra("requiredCache")
            if (webView.parent != null) {
                (webView.parent as ViewGroup).removeView(webView)
            }

            webView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            webView.visibility = View.VISIBLE
            layoutWebViewBinding.loader.visibility = View.GONE
            webView.loadUrl("javascript:window.history.pushState({}, '', $url)")
        } else {
            webView = findViewById(R.id.web_View)
            webView.visibility = View.VISIBLE

            webView.apply {
                settings.setEnableSmoothTransition(true)
                settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.textZoom = 100
//                addJavascriptInterface(
//                    AppBridge(
//                        this@WebViewActivity,
//                        webView,
//                    ), "Eka"
//                )
                isVerticalScrollBarEnabled = false
                /* webUrl?.let {
                     webUrl = BaseUtil.addUrlParam(it, "login", isFreshLogin.toString())
                 }*/
                if (showBlockingLoader)
                    layoutWebViewBinding.loader.visibility = View.VISIBLE

                loadWebUrl(webUrl)

            }
        }

        setWebviewClient()

        WebView.setWebContentsDebuggingEnabled(true)

        /* if (BuildConfig.DEBUG) {
             WebView.setWebContentsDebuggingEnabled(true)
         }*/


//        loadWebUrl(webUrl)
        /*Handler(Looper.getMainLooper()).postDelayed({
            loader.visibility = View.GONE
        }, 3000)*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    private fun loadWebUrl(url: String?) {
        if (BaseUtil.internetConnectedOrConnecting(this)) {
            var loadUrl = BaseUtil.addFlavour(url)

            if (!loadUrl.isNullOrBlank()) {
                var headers = HashMap<String, String>()
                if (application is IAmCommon) {
                    headers = (application as IAmCommon).getCommonHeaders()
                    headers["device-id"] = Utility.getDeviceId(EkaApp.instance)
                    loadUrl = BaseUtil.addUrlParam(
                        loadUrl, "Content-Language", headers["Content-Language"]
                            ?: "en"
                    )
                }
                Log.i("AppointmentActivity", "Loading URL : - $loadUrl")
                webView.loadUrl(loadUrl, headers)
            }
        } else {
            url?.let { showRetryDialog(it) }

        }

    }

    private fun setWebviewClient() {
        webView.webViewClient = webViewClient
        //limanWebview.webViewClient = webViewClient

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                // update the progressBar
                if (progress >= 80) {
                    layoutWebViewBinding.loader.visibility = View.GONE
                }
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    // Approve audio capture request
                    request.grant(request.resources)
                } else {
                    request.deny()
                }
            }

            // For Lollipop 5.0+ Devices
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
                }

                uploadMessage = filePathCallback
                //val intent = fileChooserParams.createIntent()

                val intent = getPickIntent()

//                    intent.action = Intent.ACTION_OPEN_DOCUMENT
//                    intent.addCategory(Intent.CATEGORY_OPENABLE)
//                    intent?.type = "*/*"
                intent?.let {
                    it.putExtra(Intent.EXTRA_MIME_TYPES, fileChooserParams.acceptTypes)
                    it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

                    try {
                        startActivityForResult(it, Companion.REQUEST_SELECT_FILE)
                    } catch (e: ActivityNotFoundException) {
                        uploadMessage = null
                        Toast.makeText(
                            applicationContext,
                            "Cannot Open File Chooser",
                            Toast.LENGTH_LONG
                        ).show()
                        return false
                    }
                }
                return true


            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?,
            ): Boolean {
                val newWebView = WebView(this@WebViewActivity)
                newWebView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                newWebView.addJavascriptInterface(PaymentInterface(), "PaymentInterface")
                newWebView.settings.javaScriptEnabled = true
                view?.addView(newWebView)
                val transport: WebView.WebViewTransport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?,
                    ): Boolean {
                        val browserIntent = Intent(Intent.ACTION_VIEW)
                        browserIntent.data = Uri.parse(url)
                        startActivity(browserIntent)
                        return true
                    }
                }
                return true
            }


        }
        webView.setDownloadListener { url, _, _, _, _ ->

          //  BaseUtil.openPDF(this, url)

        }
    }

    private val webViewClient = object : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if (url?.startsWith("tel:") == true) {
                try {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse(url)
                    )
                    startActivity(intent)
                    return true
                } catch (ignored: ActivityNotFoundException) {
                    // do nothing
                } catch (ex: Exception) {
                    //Handle permission denial security exception on some chinese ROM devices

                }
            }



            if (!TextUtils.isEmpty(url) && !BaseUtil.internetConnectedOrConnecting(this@WebViewActivity)) {
                showRetryDialog(url!!)
                layoutWebViewBinding.loader.visibility = View.GONE
                return true
            }

            if (!url.isNullOrBlank()) {
                val host = Uri.parse(url).host
                if (host?.contains("payment.eka.care") == true) {
                    val intent = Intent(this@WebViewActivity, WebViewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra(PageParams.PARAM_WEB_VIEW_URL, url)
                    startActivity(intent)
                    return true
                }
            }

            /* try {
                 var host = Uri.parse(url).host
                 if (!(host?.contains("eka.care")==true))
                 {
                     val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                     startActivity(intent)
                     return true
                 }
             }catch (ex:java.lang.Exception){}*/

            return false
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            if (url?.startsWith("tel:") == true) {
                try {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse(url)
                    )
                    startActivity(intent)
                } catch (ignored: ActivityNotFoundException) {
                    // do nothing
                } catch (ex: Exception) {
                    //Handle permission denial security exception on some chinese ROM devices
                }
            }
        }


        override fun onPageFinished(view: WebView, url: String) {
            layoutWebViewBinding.loader.visibility = View.GONE
            super.onPageFinished(view, url)
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            if (errorResponse.statusCode == 401) {
                Log.e("401Check", "onReceivedHttpError $errorResponse")
                /*loader.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var isTokenUpdated = false
                        for (i in 0..2) {
                            val isNewTokenUpdated: Boolean = refreshToken()
                            if (isNewTokenUpdated) {
                                isTokenUpdated = true
                                break
                            }
                        }
                        if (!isTokenUpdated) {
                            throw IOException()
                        }
                        withContext(Dispatchers.Main){
                            try {
                                loader.visibility = View.GONE
                                //loadWebUrl(webUrl)
                            } catch (e: Exception) {
                            }
                            null
                        }
                    } catch (e: IOException) {
                        UserSharedPref(application).logoutUser()
                        // What to do here??
                        withContext(Dispatchers.Main){
                            loader.visibility = View.GONE
                            if (!isFinishing()) {
                                UserSharedPref(application).logoutUser()
                                val intent = Intent(application, WalkthroughActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                application.startActivity(intent)
                            }
                            null
                        }
                    }
                }*/

            }
        }

    }

    private fun getPickIntent(): Intent? {
        val intents: MutableList<Intent> = ArrayList()
        intents.add(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intents.add(cameraIntent)

        if (intents.isEmpty()) return null
        val result = Intent.createChooser(intents.removeAt(0), null)
        if (intents.isNotEmpty()) {
            result.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intents.toTypedArray<Parcelable>()
            )
        }
        return result
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val imageFileName = "IMG_" + System.currentTimeMillis() + "_"
        val albumF: File = File(Environment.getExternalStorageDirectory().toString() + "/dcim/")
        return File.createTempFile(imageFileName, ".jpg", albumF)
    }

    private fun showRetryDialog(retryUrl: String) {
        if (isFinishing) return
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.no_network_title))
            .setMessage(resources.getString(R.string.no_network_msg))
            .setNegativeButton(resources.getString(R.string.setting)) { _, _ ->
                // Respond to negative button press
                currentRetryUrl = retryUrl
                startActivityForResult(Intent(Settings.ACTION_SETTINGS), 100)
            }
            .setPositiveButton(resources.getString(R.string.retry)) { dialog, _ ->
                // Respond to positive button press
                if (BaseUtil.internetConnectedOrConnecting(this@WebViewActivity)) {
                    if (!TextUtils.isEmpty(retryUrl)) {
                        loadWebUrl(retryUrl)
                        dialog.dismiss()
                    }
                } else {
                    if (!this@WebViewActivity.isFinishing) {
                        showRetryDialog(retryUrl)
                    }
                }
            }
            .setNeutralButton(resources.getString(R.string.close)) { _, _ ->
                // Respond to neutral button press
                finish()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (!BaseUtil.internetConnectedOrConnecting(this)) {
                currentRetryUrl?.let { showRetryDialog(it) }

            } else {
                loadWebUrl(currentRetryUrl)
            }
        } else if (requestCode == REQUEST_SELECT_CONTACT) {
            val callback = data?.getStringExtra("callback")
            val contacts = data?.getStringExtra("contacts")
            webView.loadUrl("javascript:$callback($contacts)")

        } else if (requestCode == 986) {
            data?.let {
                val paymentStatus = it.getStringExtra(PageParams.PAYMENT_STATUS)
                val pageId = it.getStringExtra(PageIdentifier.PAGE_ID)
                val webUrl = it.getStringExtra(PageParams.PARAM_WEB_VIEW_URL)
                Log.d(
                    "log", "WebViewActivity::onActivityResult" +
                            "paymentStatus = $paymentStatus, pageId = $pageId, webUrl = $webUrl"
                )
                val params = JSONObject()
                params.put(PageParams.PARAM_WEB_VIEW_URL, webUrl)
//                OrbiNavigator.navigateTo(
//                    this,
//                    pageId ?: PageIdentifier.PAGE_ID_HOMEPAGE, params
//                )
                if (paymentStatus == "success") {
                    finish()
                }
            }

        } else if (requestCode == REQUEST_GOOGLE_SIGNIN) {
            val name = data?.getStringExtra("function")
            val data = data?.getSerializableExtra("data")
            webView.loadUrl("javascript:$name(${data})")

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (requestCode == Companion.REQUEST_SELECT_FILE) {
                    if (uploadMessage != null) {
                        if (data?.extras?.get("data") != null) {
                            uploadMessage!!.onReceiveValue(arrayOf(getImageUri(data?.extras?.get("data") as Bitmap)))
                        } else if (data != null) {
                            uploadMessage?.onReceiveValue(
                                WebChromeClient.FileChooserParams.parseResult(
                                    resultCode,
                                    data
                                )
                            )
                        } else {
                            uploadMessage!!.onReceiveValue(null)

                        }
                        uploadMessage = null
                    }
                }

            } else {
                Toast.makeText(
                    this,
                    "Failed to open file uploader, please check app permissions.",
                    Toast.LENGTH_LONG
                ).show()
                super.onActivityResult(requestCode, resultCode, data)
            }

        }

    }

    open fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            inImage,
            "image",
            null
        )
        return Uri.parse(path)
    }

    private fun setupActionBar() {
        val viewActionBar = layoutWebViewBinding.topNavigation
        runOnUiThread {

            if (!showToolbar) {
                viewActionBar.root.visibility = View.GONE
                return@runOnUiThread
            } else {
                viewActionBar.root.visibility = View.VISIBLE
            }
            if (!pageTitle.isNullOrBlank()) {

                viewActionBar.tvTitle.visibility = View.VISIBLE
                viewActionBar.tvTitle.text = pageTitle
            } else {
                viewActionBar.tvTitle.visibility = View.GONE
            }

            pageTitle?.let {
                viewActionBar.tvTitle.visibility = View.VISIBLE
                viewActionBar.tvTitle.text = it
            }

            if (headerImage.isNullOrEmpty()) {
                viewActionBar.imgHeader.visibility = View.GONE
            } else {
                viewActionBar.imgHeader.visibility = View.VISIBLE
                headerImage?.let {
                    viewActionBar.imgHeader.visibility = View.VISIBLE
                    if (!this.isDestroyed) {
                        viewActionBar.imgHeader.load(it) {
                            crossfade(true)
                        }
                    }
                }
            }

            if (shareString.isNullOrEmpty()) {
                layoutWebViewBinding.topNavigation.imgRightAction.visibility = View.GONE
            } else {
                viewActionBar.imgRightAction.visibility = View.VISIBLE
                shareString?.let { text ->
                    viewActionBar.imgRightAction.visibility = View.VISIBLE
                    viewActionBar.imgRightAction.setOnClickListener {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                    }
                }
            }

            viewActionBar.imgLeftAction.setOnClickListener {
                onBackPressed()
            }
        }
    }


    internal class PaymentInterface {
        @JavascriptInterface
        fun success(data: String?) {
            Log.d("PaymentActivity Success", data ?: "Empty  data")
        }

        @JavascriptInterface
        fun error(data: String?) {
            Log.d("PaymentActivity Error", data ?: "Empty  data")
        }
    }

    companion object {
        const val REQUEST_GOOGLE_SIGNIN = 300
        const val REQUEST_SELECT_FILE = 1
        const val REQUEST_SELECT_CONTACT = 2
    }

}