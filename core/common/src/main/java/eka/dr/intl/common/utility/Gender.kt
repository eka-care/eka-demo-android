package eka.dr.intl.common.utility;

import kotlinx.serialization.Serializable

@Serializable
enum class Gender(val value: String) {
    MALE("male"),
    FEMALE("female"),
    OTHER("other"),
    UNKNOWN("unknown")
}