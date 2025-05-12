package eka.dr.intl.assistant.utility

import eka.dr.intl.icons.R

class MessageTypeMapping {
    companion object {
        fun getIcon(messageType: String): Int {
            return when (messageType) {
                MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue -> R.drawable.ic_circle_waveform_lines_regular
                else -> R.drawable.ic_messages_regular
            }
        }

        fun getSubHeadline(messageType: String): String {
            return when (messageType) {
                MessageType.TEXT.stringValue -> "Chat"
                else -> ""
            }
        }
    }
}