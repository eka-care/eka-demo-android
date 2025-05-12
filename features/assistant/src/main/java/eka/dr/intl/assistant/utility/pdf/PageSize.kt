package com.orbi.eka.base.pdf_lib.utilsx.enums

import android.util.DisplayMetrics

enum class PageSize(
    val widthPoints: Int,
    val heightPoints: Int,
    val widthInches: Double,
    val heightInches: Double,
    val widthMm: Int,
    val heightMm: Int
) {
    // A Series (ISO 216)
    A0(2384, 3370, 33.1, 46.8, 841, 1189),
    A1(1684, 2384, 23.4, 33.1, 594, 841),
    A2(1191, 1684, 16.5, 23.4, 420, 594),
    A3(842, 1191, 11.7, 16.5, 297, 420),
    A4(595, 842, 8.27, 11.7, 210, 297),
    A5(420, 595, 5.83, 8.27, 148, 210),
    A6(298, 420, 4.13, 5.83, 105, 148),

    // B Series (ISO 216)
    B0(2835, 4008, 39.4, 56.7, 1000, 1414),
    B1(2004, 2835, 28.7, 39.4, 707, 1000),
    B2(1417, 2004, 19.7, 28.7, 500, 707),
    B3(1001, 1417, 14.3, 19.7, 353, 500),
    B4(709, 1001, 10.1, 14.3, 250, 353),
    B5(499, 709, 7.17, 10.1, 176, 250),

    // North American Sizes (ANSI)
    LETTER(612, 792, 8.5, 11.0, 216, 279),
    LEGAL(612, 1008, 8.5, 14.0, 216, 356),
    TABLOID(792, 1224, 11.0, 17.0, 279, 432),
    EXECUTIVE(522, 756, 7.25, 10.5, 184, 267),
    LEDGER(1224, 792, 17.0, 11.0, 432, 279),

    // C Series (ISO 269) - Envelopes
    C0(2599, 3677, 36.1, 51.6, 917, 1297),
    C1(1837, 2599, 25.7, 36.1, 648, 917),
    C2(1298, 1837, 18.0, 25.7, 458, 648),
    C3(918, 1298, 12.8, 18.2, 324, 458);

    // Convenience function to display sizes
    fun getDimensions(): String {
        return "Size: $name, Points: $widthPoints x $heightPoints, Inches: $widthInches x $heightInches, Mm: $widthMm x $heightMm"
    }

    // Function to convert points to pixels
    fun toPixels(densityDpi: Int): Pair<Int, Int> {
        val widthPixels = (widthPoints * densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        val heightPixels = (heightPoints * densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        return Pair(widthPixels, heightPixels)
    }
}