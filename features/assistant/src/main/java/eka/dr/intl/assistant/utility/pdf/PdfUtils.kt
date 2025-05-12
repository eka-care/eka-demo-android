package eka.dr.intl.assistant.utility.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Picture
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object PdfUtils {
    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }

    fun sharePDF(file: File, context: Context) {
        try {
            val authority = "eka.care.doctor.fileprovider"
            val fileUri = FileProvider.getUriForFile(
                context,
                authority,
                file
            )

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/pdf"
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share PDF")
            shareIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            context.startActivity(shareIntent)
        } catch (ex: Exception) {
           Log.e("PrognosisFragment", "Exception in sharePDF() = ", ex)
        }
    }
}