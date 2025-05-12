package eka.dr.intl.patients.domain.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eka.dr.intl.common.Resource
import eka.dr.intl.common.utility.DateUtils.Companion.convertLongToDateString
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.AddPatientToDirectoryResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.utils.Conversions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddPatientToDirectoryUseCase(
    private val patientDirectoryRepository: PatientDirectoryRepository,
) {
    operator fun invoke(
        request: PatientEntity,
    ): Flow<Resource<AddPatientToDirectoryResponse>> = flow {
        try {
            emit(Resource.Loading())

            patientDirectoryRepository.addOrUpdatePatientToDirectory(
                request,
                !request.newPatient
            )
            emit(
                Resource.Success(
                    formatPatientEntityToAddPatientToDirectoryResponse(request)
                )
            )

        } catch (ex: Exception) {
            emit(Resource.Error(ex.localizedMessage ?: "Something went wrong!"))
        }
    }
}

fun formatPatientEntityToAddPatientToDirectoryResponse(
    patientEntity: PatientEntity,
): AddPatientToDirectoryResponse {
    val gson = Gson()
    return AddPatientToDirectoryResponse(
        status = "success",
        remark = "",
        response = AddPatientToDirectoryResponse.Response(
            pid = patientEntity.oid,
            ptProfile = AddPatientToDirectoryResponse.Response.PtProfile(
                id = patientEntity.oid,
                uuid = patientEntity.uuid,
                profile = AddPatientToDirectoryResponse.Response.PtProfile.Profile(
                    personal = AddPatientToDirectoryResponse.Response.PtProfile.Profile.Personal(
                        name = patientEntity.name,
                        age = AddPatientToDirectoryResponse.Response.PtProfile.Profile.Personal.Age(
                            dob = convertLongToDateString(patientEntity.age),
                        ),
                        phone = AddPatientToDirectoryResponse.Response.PtProfile.Profile.Personal.Phone(
                            c = patientEntity.phone.toString(),
                            n = "+${patientEntity.countryCode}",
                        ),
                        gender = Converters().fromGenderToString(patientEntity.gender),
                        bloodgroup = Converters().formBloodGroupToString(patientEntity.bloodGroup),
                        onApp = patientEntity.onApp,
                    )

                ),
                formData = gson.fromJson(
                    patientEntity.formData,
                    object : TypeToken<List<PatientDirectoryResponse.Patient.FormData?>>() {}.type
                ),
                archived = patientEntity.archived,
                link = arrayListOf(),
                createdAt = Conversions.fromLongToTimestamp(patientEntity.createdAt),
                updatedAt = Conversions.fromLongToTimestamp(patientEntity.updatedAt),
                referredBy = patientEntity.referredBy,
                referId = patientEntity.referId,
            )
        )
    )
}