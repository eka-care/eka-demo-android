package eka.dr.intl.common.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import eka.dr.intl.common.DateTimeUtility
import eka.dr.intl.common.PageParams
import eka.dr.intl.common.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class BaseUtil {

    companion  object {

        fun internetConnectedOrConnecting(context: Context): Boolean {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var internetConnected = false
            val activeInfo = connectivity.activeNetworkInfo
            if (activeInfo != null) {
                internetConnected = activeInfo.isConnectedOrConnecting
            } else {
                val info = connectivity.allNetworkInfo
                for (anInfo in info) {
                    if (anInfo.isConnectedOrConnecting) {
                        internetConnected = true
                    }
                }
            }
            return internetConnected
        }

        fun getTimeOfDay(context: Context): String {
            val curTimeOfDay = DateTimeUtility.getCurrentTime()
            Log.d("log", "curTimeOfDay = $curTimeOfDay")

            if (curTimeOfDay > DateTimeUtility.getTimeAtHourOfDay(0) &&
                curTimeOfDay < DateTimeUtility.getTimeAtHourOfDay(12)
            ) {

                // Until 12 PM
                return context.getString(R.string.morning)

            } else if (curTimeOfDay >= DateTimeUtility.getTimeAtHourOfDay(12) &&
                curTimeOfDay < DateTimeUtility.getTimeAtHourOfDay(16)
            ) {
                // From 12PM to 4PM
                return context.getString(R.string.afternoon)
            } else {
                return context.getString(R.string.evening)
            }
        }

        fun addFlavour(url: String?): String? {
            if (url == null || TextUtils.isEmpty(url.trim { it <= ' ' })) {
                // null or empty url
                return ""
            }
            if (url.contains("flavour=android")) {
                return url
            }
            return if (url.split("\\?".toRegex())
                    .toTypedArray().size > 1
            ) "$url&flavour=android" else "$url?flavour=android"
        }

        fun addUrlParam(url: String, key: String, value: String): String {
            if (url.contains("$key=$value")) {
                return url
            }
            return if (url.split("\\?".toRegex())
                    .toTypedArray().size > 1
            ) "$url&$key=$value" else "$url?$key=$value"
        }

        fun hideKeyboard(activity: Activity) {
            try {
                val imm: InputMethodManager =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view: View? = activity.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (ex: Exception) {
                Log.e("log", "Exception in hideKeyboard() = ", ex)
            }
        }

        fun print(context: Context, url: String) {
            val pds = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled() == true) {
                        callback?.onLayoutCancelled();
                        return;
                    }
                    var pdi = PrintDocumentInfo.Builder("NAME OF DOCUMENT")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    callback?.onLayoutFinished(pdi, true);

                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: WriteResultCallback?
                ) {

                    Thread {
                        var input: InputStream? = null;
                        var output: OutputStream? = null

                        try {
                            input = URL(url).openStream();
                            output = FileOutputStream(destination?.fileDescriptor);

                            val buf = ByteArray(1024)
                            var bytesRead: Int
                            bytesRead = input.read(buf)
                            while (bytesRead > 0) {
                                output.write(buf, 0, bytesRead);
                                bytesRead = input.read(buf)
                            }

                            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

                        } catch (_: FileNotFoundException) {
                        } catch (_: Exception) {
                        } finally {
                            try {
                                input?.close();
                                output?.close();
                            } catch (e: IOException) {
                                e.printStackTrace();
                            }
                        }
                    }.start()

                }


            }

            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print("JOB NAME", pds, null);
            /* PrintDocumentAdapter pda = new PrintDocumentAdapter() {

                 @Override
                 public void onWrite(PageRange[] pages, final ParcelFileDescriptor destination, CancellationSignal cancellationSignal, final WriteResultCallback callback) {

                     !!THIS MUST BE RUN ASYNC!!

                     InputStream input = null;
                     OutputStream output = null;

                     try {
                         input = new URL(YOUR URL HERE).openStream();
                         output = new FileOutputStream(destination.getFileDescriptor());

                         byte[] buf = new byte[1024];
                         int bytesRead;

                         while ((bytesRead = input.read(buf)) > 0) {
                             output.write(buf, 0, bytesRead);
                         }

                         callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                     } catch (FileNotFoundException ee) {
                         //TODO Handle Exception
                     } catch (Exception e) {
                         //TODO Handle Exception
                     } finally {
                         try {
                             input.close();
                             output.close();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 }

                 @Override
                 public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                     if (cancellationSignal.isCanceled()) {
                         callback.onLayoutCancelled();
                         return;
                     }
                     PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("NAME OF DOCUMENT").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                     callback.onLayoutFinished(pdi, true);
                 }
             };

             PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
             printManager.print("JOB NAME", pda, null);*/
        }


        fun getPixels(context: Context, valueInDp: Int): Int {
            val r = context.resources
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                valueInDp.toFloat(),
                r.displayMetrics
            )
            return px.toInt()
        }

        fun isDeviceRooted(): Boolean {
            val buildTags = android.os.Build.TAGS
            if (buildTags != null && buildTags.contains("test-keys")) {
                return true
            }

            return try {
                val file = File("/system/app/Superuser.apk")
                file.exists()
            } catch (e: Exception) {
                false
            }
        }

        fun dpToPixel(context: Context, dp: Int): Int {
            val r: Resources = context.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            ).toInt()
        }
    }
}