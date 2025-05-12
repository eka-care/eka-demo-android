package eka.dr.intl.utility

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.provider.Settings
import android.util.TypedValue
import eka.dr.intl.EkaApp
import eka.dr.intl.common.UserSharedPref

class Utility {
    companion object {

        fun getBasicHeaders(): HashMap<String, String> {
            val headers = HashMap<String, String>()
            headers["auth"] = UserSharedPref.invoke(EkaApp.instance).getSessionToken() ?: ""
            headers["flavour"] = "android"
            headers["version"] = getAppVersion(EkaApp.instance).toString()
            headers["client-id"] = "androiddoc"
            EkaApp.instance.sessionData.forEach { (key, value) ->
                headers[key] = value ?: "NA"
            }
            return headers
        }

        fun dpToPixel(dp: Int): Int {
            val r: Resources = EkaApp.instance.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            ).toInt()
        }


        /**
         * @return Application's version code from the `PackageManager`.
         */
        fun getAppVersion(context: Context): Int {
            return try {
                val packageInfo = context.packageManager
                    .getPackageInfo(context.packageName, 0)
                packageInfo.versionCode
            } catch (e: NameNotFoundException) {
                // should never happen
                throw RuntimeException("Could not get package name: $e")
            }
        }

        fun getDeviceId(context: Context): String {
            var deviceId = ""
            try {
                deviceId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            } catch (_: Exception) {
            }
            return deviceId
        }

//        fun showConfirmPopup(
//            context: Context,
//            message: String,
//            title: String? = null,
//            action: () -> Unit
//        ) {
//            val alertDialogBuilder = AlertDialog.Builder(context)
//            // set the custom layout
//            val layoutInflater =
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val customLayout: View = layoutInflater.inflate(R.layout.confirm_popup_layout, null)
//            alertDialogBuilder.setView(customLayout)
//            val alertDialog = alertDialogBuilder.show()
//            val titleView = customLayout.findViewById<TextView>(R.id.tv_title)
//            if (!title.isNullOrEmpty()) {
//                titleView.text = title
//                titleView.visibility = View.VISIBLE
//            }
//
//            val tvNo = customLayout.findViewById<TextView>(R.id.tv_no)
//            val tvYes = customLayout.findViewById<TextView>(R.id.tv_yes)
//            val tvConfirm = customLayout.findViewById<TextView>(R.id.tv_confirm)
//            tvConfirm.text = message
//
//            tvNo.setOnClickListener {
//                alertDialog.dismiss()
//            }
//
//            tvYes.setOnClickListener {
//                action()
//                alertDialog.dismiss()
//            }
//        }

//        fun getDrawableFromIconName(name: String?, context: Context): Drawable? {
//            return when (name) {
//                "message" -> {
//                    context.getDrawable(R.drawable.ic_message_brand)
//                }
//
//                "phone" -> {
//                    context.getDrawable(R.drawable.ic_call_brand)
//                }
//
//                "user" -> {
//                    context.getDrawable(R.drawable.ic_user_brand)
//                }
//
//                "calendar" -> context.getDrawable(R.drawable.ic_calendar)
//                "clipboard-list" -> context.getDrawable(R.drawable.ic_list_grey)
//                "users" -> context.getDrawable(R.drawable.ic_users_grey)
//                "Phone" -> {
//                    context.getDrawable(R.drawable.ic_call_dark_grey)
//                }
//
//                "percent" -> {
//                    context.getDrawable(R.drawable.ic_percentage_grey)
//                }
//
//                "video" -> {
//                    context.getDrawable(R.drawable.ic_tele_brand)
//                }
//
//                else -> null
//            }
//        }


        fun getHashCode(vararg args: String): Int {
            var hash = 0
            try {
                for (arg in args) {
                    hash += arg.hashCode()
                }
            } catch (_: Exception) {
            }

            return hash
        }

//        fun showCustomInAppRatingPopup(context: Context): Boolean {
//            try {
//                /*val lastTime: Long = (context.applicationContext as IAmCommon).getValue(
//                    "in_app_rating_time",
//                    0L
//                )?.plus(1000 * 60 * 60 * 24 * 7) ?: 0
//
//                if (System.currentTimeMillis() <= lastTime) {
//                    return false
//                }*/
//                (context.applicationContext as IAmCommon).setValue(
//                    "in_app_rating_time",
//                    System.currentTimeMillis()
//                )
//                OrbiLogger.d("log", "In showInAppRatingPopup()")
//
//                val alertDialogBuilder = AlertDialog.Builder(context)
//                val inAppRatingView = InAppRatingView(context)
//                alertDialogBuilder.setView(inAppRatingView)
//                val alertDialog = alertDialogBuilder.show()
//
//                inAppRatingView.setAlertDialog(alertDialog)
//
//                val params = JSONObject()
//                params.put("type", "rating_popup_init")
//                AnalyticsUtil.sendPageViewEvent(context, "custom_rating_popup", params)
//            } catch (ex: Exception) {
//                OrbiLogger.e("log", "Exception in = showInAppRatingPopup()", ex)
//            }
//            return true
//        }
//
//
//        fun showGoogleInAppRatingPopup(currentActivity: Activity): Boolean {
//            try {
//                (currentActivity.applicationContext as IAmCommon).setValue(
//                    "in_app_rating_time",
//                    System.currentTimeMillis()
//                )
//                OrbiLogger.d("log", "In showInAppRatingPopup()")
//
//                val params = JSONObject()
//                params.put("type", "rating_popup_init")
//                AnalyticsUtil.sendPageViewEvent(currentActivity, "inApp_rating_popup", params)
//            } catch (ex: Exception) {
//                OrbiLogger.e("log", "Exception in = showInAppRatingPopup()", ex)
//            }
//            return true
//        }

    }

}