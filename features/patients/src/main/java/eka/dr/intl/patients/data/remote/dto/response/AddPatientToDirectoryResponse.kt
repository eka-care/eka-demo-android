package eka.dr.intl.patients.data.remote.dto.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddPatientToDirectoryResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("remark") var remark: String? = null,
    @SerializedName("response") var response: Response? = Response(),
) {
    data class Response(
        @SerializedName("pid") var pid: String? = null,
        @SerializedName("ptProfile") var ptProfile: PtProfile? = PtProfile()
    ) {
        data class PtProfile(
            @SerializedName("id") var id: String? = null,
            @SerializedName("uuid") var uuid: String? = null,
            @SerializedName("profile") var profile: Profile? = Profile(),
            @SerializedName("formData") var formData: List<PatientDirectoryResponse.Patient.FormData> = arrayListOf(),
            @SerializedName("archived") var archived: Boolean? = null,
            @SerializedName("link") var link: List<String> = arrayListOf(),
            @SerializedName("created_at") var createdAt: String? = null,
            @SerializedName("updated_at") var updatedAt: String? = null,
            @SerializedName("referredBy") var referredBy: String? = null,
            @SerializedName("refer_id") var referId: String? = null

        ) {
            data class Profile(
                @SerializedName("personal") var personal: Personal? = Personal()

            ) {
                data class Personal(
                    @SerializedName("name") var name: String? = null,
                    @SerializedName("age") var age: Age? = Age(),
                    @SerializedName("phone") var phone: Phone? = Phone(),
                    @SerializedName("gender") var gender: String? = null,
                    @SerializedName("bloodgroup") var bloodgroup: String? = null,
                    @SerializedName("onApp") var onApp: Boolean? = null
                ) {
                    data class Age(
                        @SerializedName("dob") var dob: String? = null
                    )

                    data class Phone(
                        @SerializedName("c") var c: String? = null,
                        @SerializedName("n") var n: String? = null

                    )
                }
            }
        }
    }
}