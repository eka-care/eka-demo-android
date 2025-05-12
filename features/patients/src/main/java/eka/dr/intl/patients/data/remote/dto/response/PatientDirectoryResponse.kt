package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PatientDirectoryResponse(
    @SerializedName("status") var status: String? = "",
    @SerializedName("data") var data: List<Patient> = emptyList(),
    @SerializedName("version") var version: Int? = null,
    @SerializedName("currPageMeta") var currPageMeta: CurrPageMeta? = CurrPageMeta()
) {
    @Keep
    data class Patient(
        @SerializedName("id") var id: String? = null,
        @SerializedName("poid") var pOid: String? = null,
        @SerializedName("uuid") var uuid: String? = null,
        @SerializedName("profile") var profile: Profile? = null,
        @SerializedName("formData") var formData: List<FormData?> = emptyList(),
        @SerializedName("archived") var archived: Boolean? = null,
        @SerializedName("link") var link: List<String?>? = emptyList(),
        @SerializedName("abha") var abha: Abha? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("referredBy") var referredBy: String? = null,
        @SerializedName("refer_id") var referId: String? = null,
        @SerializedName("visits") var visits: List<Visits?>? = emptyList(),
        @SerializedName("followup") var followup: String? = null,
        @SerializedName("last_visit") var lastVisit: String? = "",
    ) {
        @Keep
        data class Abha(
            @SerializedName("id") var id: String? = null,
            @SerializedName("records") var records: Int? = null,
            @SerializedName("status") var status: String? = null
        )

        @Keep
        data class FormData(
            @SerializedName("label") var label: String? = "",
            @SerializedName("key") var key: String? = "",
            @SerializedName("type") var type: String? = "",
            @SerializedName("value") var value: Any? = null
        )

        @Keep
        data class Visits(
            @SerializedName("clinicid") val clinicId: String,
            @SerializedName("name") val clinicName: String,
            @SerializedName("pxid") val pxId: String,
            @SerializedName("status") val status: String,
            @SerializedName("url") val url: String,
            @SerializedName("vid") val vid: String,
            @SerializedName("visit_date") val visitDate: String
        )

        @Keep
        data class Profile(
            @SerializedName("personal") var personal: Personal
        ) {
            @Keep
            data class Personal(
                @SerializedName("name") var name: String,
                @SerializedName("age") var age: Age? = Age(),
                @SerializedName("phone") var phone: Phone = Phone(),
                @SerializedName("gender") var gender: String? = null,
                @SerializedName("bloodgroup") var bloodgroup: String? = null,
                @SerializedName("onApp") var onApp: Boolean? = null,
                @SerializedName("health-ids") var healthIds: List<String?> = listOf()

            ) {
                @Keep
                data class Age(
                    @SerializedName("dob") var dob: String? = null
                )

                @Keep
                data class Phone(
                    @SerializedName("c") var c: String? = null,
                    @SerializedName("n") var n: String? = null
                )
            }
        }
    }

    @Keep
    data class CurrPageMeta(
        @SerializedName("currPageNo") var currPageNo: Int? = null,
        @SerializedName("pageSize") var pageSize: Int? = null,
        @SerializedName("totalRecords") var totalRecords: Int? = null,
        @SerializedName("totalPages") var totalPages: Int? = null,
        @SerializedName("nextPage") var nextPage: Int? = null
    )
}