package eka.dr.intl.common.utility

typealias NavigateToMedicalRecords = (filterId: String, ownerId: String, links: String?, name: String?, gen: String?, age: Int?) -> Unit
typealias NavigateToAddPatient = (data: String?, from: String?, phone: String?, email: String?, name: String?) -> Unit
typealias NavigateToPatientActionScreen = ((pid: String) -> Unit)?
typealias NavigateToTranscriptScreen = (sessionId: String, transcript: String?) -> Unit