package eka.dr.intl.common.presentation.state

import eka.dr.intl.common.presentation.component.ConsultationState
import eka.dr.intl.common.presentation.component.NetworkState

sealed class DocStatus {
    data object NoneStatus : DocStatus()
    data class NetworkStatus(val status: NetworkState) : DocStatus()
    data class ConsultationStatus(val state: ConsultationState) : DocStatus()
}