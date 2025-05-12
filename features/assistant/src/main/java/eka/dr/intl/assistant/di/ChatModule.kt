package eka.dr.intl.assistant.di

import eka.dr.intl.assistant.di.EkaChatKoin.Companion.EKA_CHAT_REPOSITORY_IMPL
import eka.dr.intl.assistant.data.repository.EkaChatRepositoryImpl
import eka.dr.intl.assistant.domain.repository.EkaChatRepository
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.presentation.viewmodel.Voice2RxViewModel
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val chatModule = module {
    viewModel {
        EkaChatViewModel(get())
    }
    viewModel {
        Voice2RxViewModel(get())
    }
    viewModel {
        DoctorStatusViewModel(get())
    }
}

private val domainModule = module {
    single<EkaChatRepository>(named(EKA_CHAT_REPOSITORY_IMPL)) {
        EkaChatRepositoryImpl()
    }
}

private val listOfChatModule =
    listOf(chatModule, domainModule)

fun getChatModules() = listOfChatModule
