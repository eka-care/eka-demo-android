package eka.dr.intl.patients.presentation.state

data class UiState<T>(
    val loading: Boolean = false,
    val error: String? = null,
    val data: T? = null
)