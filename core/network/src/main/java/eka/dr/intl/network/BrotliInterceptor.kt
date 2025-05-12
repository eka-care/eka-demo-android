package eka.dr.intl.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.brotli.dec.BrotliInputStream
import java.io.ByteArrayInputStream

class BrotliInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.header("Content-Encoding") == "br") {
            val compressedResponseBody = originalResponse.body?.bytes()
            val brotliInputStream = BrotliInputStream(ByteArrayInputStream(compressedResponseBody))
            val decompressedBytes = brotliInputStream.readBytes()
            val decompressedResponseBody =
                decompressedBytes.toResponseBody(originalResponse.body?.contentType())
            return originalResponse.newBuilder()
                .body(decompressedResponseBody)
                .removeHeader("Content-Encoding")
                .build()
        }
        return originalResponse
    }
}