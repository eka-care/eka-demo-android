package eka.dr.intl.assistant.utility.pdf

import android.content.Context
import com.orbi.eka.base.pdf_lib.utilsx.enums.PageSize

val defaultConfig = Config(
    pageSize = PageSize.A4, pageOrientation = PageOrientation.PORTRAIT
)

class GeneratePdfLibrary private constructor(
    private val config: Config = defaultConfig
) {
    private val pageOrientation = config.pageOrientation

    companion object {
        @Volatile
        private var instance: GeneratePdfLibrary? = null

        fun getInstance(config: Config = defaultConfig): GeneratePdfLibrary {
            return instance ?: synchronized(this) {
                instance ?: GeneratePdfLibrary(config).also { instance = it }
            }
        }
    }


    fun getPageDimensions(
        context: Context,
        contentWidth: Int,
        contentHeight: Int
    ): Triple<Int, Int, Float> {
        val dpi = context.resources.displayMetrics.densityDpi
        val pageSize = config.pageSize.toPixels(dpi)

        val (pageWidth, pageHeight) = if (pageOrientation == PageOrientation.PORTRAIT) {
            pageSize.first to pageSize.second
        } else {
            pageSize.second to pageSize.first
        }

        val pageScaleFactor = 0.9f
        val availableWidth = pageWidth * pageScaleFactor
        val availableHeight = pageHeight * pageScaleFactor

        val widthScale = availableWidth / contentWidth
        val heightScale = availableHeight / contentHeight

        val scale = minOf(widthScale, heightScale)

        return Triple(pageWidth, pageHeight, scale)
    }

}