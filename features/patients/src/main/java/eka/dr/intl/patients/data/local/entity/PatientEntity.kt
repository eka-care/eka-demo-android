package eka.dr.intl.patients.data.local.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import eka.dr.intl.common.utility.BloodGroup
import eka.dr.intl.common.utility.Gender
import eka.dr.intl.patients.data.local.convertors.Converters

@Fts4(contentEntity = PatientEntity::class)
@Entity(tableName = "patient_entity_fts")
data class PatientEntityFts(
    @PrimaryKey
    @ColumnInfo(name = "rowid") val rowid: Int,
    @ColumnInfo(name = "oid") val oid: String = "",
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "phone") val phone: String? = "",
    @ColumnInfo(name = "uhid") val uhid: String? = "",
)

@Entity(
    tableName = "patient_entity",
    indices = [Index(value = ["oid"], unique = true),
        Index(value = ["uhid"]),
        Index(value = ["on_app"])]
)
@TypeConverters(Converters::class)
data class PatientEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "oid") val oid: String,
    @ColumnInfo(name = "business_id") val businessId: String,
    @ColumnInfo(name = "dirty") val dirty: Boolean = false,
    @ColumnInfo(name = "new_patient") val newPatient: Boolean = false,
    @ColumnInfo(name = "uuid") val uuid: String? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "age") val age: Long? = null,
    @ColumnInfo(name = "countryCode") val countryCode: Int? = null,
    @ColumnInfo(name = "phone") val phone: Long? = null,
    @ColumnInfo(name = "gender") val gender: Gender,
    @ColumnInfo(name = "blood_group") val bloodGroup: BloodGroup? = null,
    @ColumnInfo(name = "on_app") val onApp: Boolean = false,
    @ColumnInfo(name = "uhid") val uhid: String? = null,
    @ColumnInfo(name = "form_data") val formData: String? = null,
    @ColumnInfo(name = "archived") val archived: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "referred_by") val referredBy: String? = null,
    @ColumnInfo(name = "refer_id") val referId: String? = "",
    @ColumnInfo(name = "links") val links: List<String?>? = emptyList(),
    @ColumnInfo(name = "follow_up") val followUp: Long? = null,
    @ColumnInfo(name = "last_visit") val lastVisit: Long? = null,
)


data class VisitsEntity(
    @ColumnInfo(name = "px_id") val pxId: String? = "",
    @ColumnInfo(name = "clinic_id") val clinicId: String? = "",
    @ColumnInfo(name = "clinic_name") val clinicName: String? = "",
    @ColumnInfo(name = "status") val status: String? = "",
    @ColumnInfo(name = "url") val url: String? = "",
    @ColumnInfo(name = "vid") val vid: String? = "",
    @ColumnInfo(name = "visit_date") val visitDate: String? = ""
)

@Keep
data class FormDataEntity(
    @ColumnInfo(name = "label") val label: String? = "",
    @ColumnInfo(name = "key") val key: String? = "",
    @ColumnInfo(name = "type") val type: String? = "",
    @ColumnInfo(name = "value") val value: Any? = null
)