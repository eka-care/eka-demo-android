package eka.dr.intl.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.di.ExploreEkaKoin
import eka.dr.intl.domain.usecase.GetAllWorkspaceUseCase
import eka.dr.intl.domain.usecase.SignOutWorkspaceUseCase
import eka.dr.intl.domain.usecase.SwitchWorkspaceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class DrawerViewModel : ViewModel(), KoinComponent {
    private val getAllWorkspaceUseCase: GetAllWorkspaceUseCase by inject<GetAllWorkspaceUseCase>(
        named(ExploreEkaKoin.GET_ALL_WORKSPACES_USE_CASE)
    )
    private val switchWorkspaceUseCase: SwitchWorkspaceUseCase by inject<SwitchWorkspaceUseCase>(
        named(ExploreEkaKoin.SWITCH_WORKSPACE_USE_CASE)
    )
    private val signOutWorkspaceUseCase: SignOutWorkspaceUseCase by inject<SignOutWorkspaceUseCase>(
        named(ExploreEkaKoin.SIGN_OUT_WORKSPACE_USE_CASE)
    )

    private val _workSpacesUiState = MutableStateFlow<List<BusinessEntity>>(emptyList())
    val workSpacesUiState = _workSpacesUiState.asStateFlow()

    fun isLoggedInToAnyWorkspace(logout: () -> Unit) {
        getAllWorkspaceUseCase.invoke().onEach { workspaces ->
            if (workspaces.isEmpty()) {
                logout.invoke()
            }
        }.launchIn(viewModelScope)
    }

    fun getAllWorkspace() {
        getAllWorkspaceUseCase.invoke().onEach { workspaces ->
            _workSpacesUiState.value = workspaces
        }.launchIn(viewModelScope)
    }

    fun switchWorkspace(switchToWorkspace: BusinessEntity) {
        switchWorkspaceUseCase.invoke(
            switchToWorkspace = switchToWorkspace
        ).onEach {
            if (it) {
                getAllWorkspace()
            }
        }.launchIn(viewModelScope)
    }

    fun signOutWorkspace(
        workspace: BusinessEntity,
        onLogout: () -> Unit,
        onSwitch: (switchTo: BusinessEntity) -> Unit
    ) {
        signOutWorkspaceUseCase.invoke(
            businessEntity = workspace,
            onLogout = onLogout,
            onSwitch = onSwitch
        ).onEach {
            if (it) {
                getAllWorkspace()
            }
        }.launchIn(viewModelScope)
    }
}