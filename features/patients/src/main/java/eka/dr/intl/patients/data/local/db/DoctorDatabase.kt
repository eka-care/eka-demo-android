package eka.dr.intl.patients.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.dao.DoctorDao
import eka.dr.intl.patients.data.local.dao.ExploreEkaDao
import eka.dr.intl.patients.data.local.dao.HistoryDao
import eka.dr.intl.patients.data.local.dao.PatientDao
import eka.dr.intl.patients.data.local.entity.DoctorEntity
import eka.dr.intl.patients.data.local.entity.ExploreEkaEntity
import eka.dr.intl.patients.data.local.entity.HistoryEntity
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.local.entity.PatientEntityFts

@Database(
    entities = [
        PatientEntity::class,
        PatientEntityFts::class,
        HistoryEntity::class,
        ExploreEkaEntity::class,
        DoctorEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DoctorDatabase : RoomDatabase() {
    abstract fun dao(): PatientDao
    abstract fun historyDao(): HistoryDao
    abstract fun exploreEkaDao(): ExploreEkaDao
    abstract fun doctorDao(): DoctorDao


    companion object {
        const val DATABASE_NAME = "doctor_db"
    }
}
