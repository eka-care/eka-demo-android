package eka.dr.intl.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import eka.dr.intl.data.local.dao.BusinessDao
import eka.dr.intl.data.local.entity.BusinessEntity

@Database(
    entities = [BusinessEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao

    companion object {
        const val WORKSPACE_DATABASE = "workspace_db"
    }
}
