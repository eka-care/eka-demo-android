package eka.dr.intl.patients.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctor_entity")
data class DoctorEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "fln") val fln: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "specialisation") val specialisation: String? = null,
    @ColumnInfo(name = "dob") val dob: String,
    @ColumnInfo(name = "pic") val pic: String,
    @ColumnInfo(name = "business_id") val businessId: String
)


