package eka.dr.intl.patients.utils


enum class DynamicFormKeys(val type: String) {
    SALUTATION("salutation"),
    PATIENT_ADDRESS("paddress"),
    CITY("city"),
    PINCODE("pincode"),
    EMAIL("email"),
    BLOOD_GROUP("bloodgroup"),
    ALTERNATIVE_PHONE("aphone"),
    REFERRED_BY_DOCTOR("referredby"),
    REFERRED_DOCTOR_NUMBER("referredbynumber"),
    MARITAL_STATUS("maritalstatus"),
    NAME_OF_INFORMANT("noi"),
    CHANNEL("channel"),
    PATIENT_OCCUPATION("pocc"),
    GUARDIAN_NAME("gnn"),
    RELATIONSHIP_WITH_PATIENT("rpp"),
    PHONE_COUNTRY_CODE("pcc"),
}