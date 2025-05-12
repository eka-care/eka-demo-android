package eka.dr.intl.di

import androidx.room.Room
import eka.dr.intl.di.DatabaseKoin.Companion.WORKSPACE_DATABASE_MODULE
import eka.dr.intl.data.local.db.WorkspaceDatabase
import eka.dr.intl.data.local.db.WorkspaceDatabase.Companion.WORKSPACE_DATABASE
import org.koin.core.qualifier.named
import org.koin.dsl.module


private val databaseModule = module {
    single<WorkspaceDatabase>(named(WORKSPACE_DATABASE_MODULE)) {
        Room.databaseBuilder(
            get(),
            WorkspaceDatabase::class.java,
            WORKSPACE_DATABASE
        ).fallbackToDestructiveMigration().build()
    }
}

val patientDatabaseModuleList = listOf(databaseModule)

fun getDatabaseModule() = patientDatabaseModuleList