package eka.dr.intl.assistant.utility.pdf

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.orbi.eka.base.pdf_lib.utilsx.enums.PageSize

@Keep
@Stable
data class Config(
    @Stable val pageSize: PageSize = PageSize.A4,
    @Stable val pageOrientation: PageOrientation = PageOrientation.PORTRAIT,
)

@Keep
@Stable
data class PdfConfig(
    @Stable val name: String,
    @Stable val header: (@Composable () -> Unit),
    @Stable val footer: (@Composable () -> Unit),
    @Stable val body: (@Composable () -> Unit),
)


@Keep
@Stable
data class SinglePdfConfig(
    @Stable val name: String,
    @Stable val jView: (@Composable () -> Unit)
)