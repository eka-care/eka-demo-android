package eka.dr.intl.patients.data.remote.api

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.patients.data.remote.dto.response.GenerateUHIDResponse
import eka.dr.intl.patients.data.remote.dto.response.GetByMobileResponse
import eka.dr.intl.patients.data.remote.dto.response.GetFormFieldsResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientAssetsUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface PatientDirectory {
    @Multipart
    @POST("/service/patient/assets/upload")
    suspend fun patientAssetsUpload(
        @QueryMap map: HashMap<String, String>,
        @Part("patient-pic") patientPic: MultipartBody.Part?,
    ): NetworkResponse<PatientAssetsUploadResponse, PatientAssetsUploadResponse>

    @GET("/app/uhid/suggest/")
    suspend fun generateUHID(
        @QueryMap map: HashMap<String, String>,
    ): NetworkResponse<GenerateUHIDResponse, GenerateUHIDResponse>

    @GET("profile/get-by-mobile/")
    suspend fun getByMobile(
        @QueryMap map: HashMap<String, String>,
    ): NetworkResponse<GetByMobileResponse, GetByMobileResponse>

    @GET("/app/get/form/")
    suspend fun getFormFields(): NetworkResponse<GetFormFieldsResponse, GetFormFieldsResponse>
}
