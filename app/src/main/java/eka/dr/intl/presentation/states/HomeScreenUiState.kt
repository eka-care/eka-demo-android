package eka.dr.intl.presentation.states

import eka.dr.intl.presentation.model.HomeDomain

sealed class HomeScreenUiState {
    data object StateLoading : HomeScreenUiState()
    data class StateError(val msg: String) : HomeScreenUiState()
    data class StateSuccess(val data: HomeDomain?) : HomeScreenUiState()
}