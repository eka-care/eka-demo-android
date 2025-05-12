package eka.dr.intl.patients.data.remote.api

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.patients.data.remote.dto.request.AddPatientRequest
import eka.dr.intl.patients.data.remote.dto.request.UpdatePatientRequest
import eka.dr.intl.patients.data.remote.dto.response.AddPatientResponse
import eka.dr.intl.patients.data.remote.dto.response.ArchivePatientResponse
import eka.dr.intl.patients.data.remote.dto.response.GetPatientByIdResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PatientDirectoryNew {
    @DELETE("/profiles/v1/business-patient/{patientOid}")
    suspend fun archivePatient(
        @Path("patientOid") patientOid: String,
    ): NetworkResponse<ArchivePatientResponse, ArchivePatientResponse>

    @POST("/profiles/v1/business-patient")
    suspend fun addPatientToDirectory(@Body data: AddPatientRequest): NetworkResponse<AddPatientResponse, AddPatientResponse>

    @PATCH("/profiles/v1/business-patient/{patientOid}")
    suspend fun updatePatientInDirectory(
        @Path("patientOid") patientOid: String,
        @Body data: UpdatePatientRequest
    ): NetworkResponse<AddPatientResponse, AddPatientResponse>

    @GET("/profiles/v1/business-patient/")
    suspend fun getPatientDirectory(
        @QueryMap map: HashMap<String, String>,
        @HeaderMap headers: HashMap<String, String>
    ): NetworkResponse<PatientDirectoryResponse, PatientDirectoryResponse>

    @GET("/profiles/v1/business-patient/{patientOid}")
    suspend fun getPatientById(
        @Path("patientOid") patientOid: String,
    ): NetworkResponse<GetPatientByIdResponse, GetPatientByIdResponse>

}

