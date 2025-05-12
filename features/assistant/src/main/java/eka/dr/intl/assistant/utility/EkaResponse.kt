package eka.dr.intl.assistant.utility

sealed class EkaResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : EkaResponse<T>(data)

    class Error<T>(message: String? = null, data: T? = null) : EkaResponse<T>(data, message)

    class Loading<T> : EkaResponse<T>()
}