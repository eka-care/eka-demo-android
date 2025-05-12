package eka.dr.intl.di

import eka.dr.intl.data.repository.BusinessStoreRepositoryImpl
import eka.dr.intl.domain.repository.BusinessStoreRepository
import eka.dr.intl.domain.usecase.AddWorkspaceUseCase
import eka.dr.intl.domain.usecase.GetAllWorkspaceUseCase
import eka.dr.intl.domain.usecase.SignOutWorkspaceUseCase
import eka.dr.intl.domain.usecase.SwitchWorkspaceUseCase
import eka.dr.intl.domain.usecase.UpdateSpecialisationWorkspaceUseCase
import eka.dr.intl.presentation.viewModel.DrawerViewModel
import eka.dr.intl.presentation.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(
            get()
        )
    }
    viewModel {
        DrawerViewModel()
    }
}

private val exploreEkaDataModule = module {
    single<BusinessStoreRepository>(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)) {
        BusinessStoreRepositoryImpl(get(named(ExploreEkaKoin.WORKSPACE_DATABASE_MODULE)))
    }
}

private val exploreEkaUseCaseModule = module {
    factory(named(ExploreEkaKoin.ADD_WORKSPACE_USE_CASE)) {
        AddWorkspaceUseCase(get(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)))
    }
    factory(named(ExploreEkaKoin.GET_ALL_WORKSPACES_USE_CASE)) {
        GetAllWorkspaceUseCase(get(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)))
    }
    factory(named(ExploreEkaKoin.SIGN_OUT_WORKSPACE_USE_CASE)) {
        SignOutWorkspaceUseCase(get(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)))
    }
    factory(named(ExploreEkaKoin.SWITCH_WORKSPACE_USE_CASE)) {
        SwitchWorkspaceUseCase(get(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)))
    }
    factory(named(ExploreEkaKoin.UPDATE_SPECIALISATION_WORKSPACE_USE_CASE)) {
        UpdateSpecialisationWorkspaceUseCase(get(named(ExploreEkaKoin.BUSINESS_STORE_REPOSITORY_IMPL)))
    }
}

val listOfExploreEkaModule = listOf(exploreEkaDataModule, exploreEkaUseCaseModule, homeModule)

fun getExploreEkaModules() = listOfExploreEkaModule
