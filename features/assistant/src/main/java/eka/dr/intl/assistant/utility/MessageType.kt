package eka.dr.intl.assistant.utility

enum class MessageType(
    val stringValue: String
) {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    PDF("PDF"),
    AUDIO("AUDIO"),
    VOICE_2_RX_PRESCRIPTION_DRAFT("V2RXDRAFT"),
    VOICE_2_RX_PRESCRIPTION_COMPLETED("V2RXCOMPLETE"),
    VOICE_2_RX_ERROR("V2RXERR"),
    TAG("TAG")
}