package eka.dr.intl.common.utility

import java.security.SecureRandom
import java.util.Calendar

class ProfileHelper {
    companion object {
        fun generateUniqueOid(): String {
            val calendar = Calendar.getInstance()
            val timestamp = calendar.timeInMillis

            val random = SecureRandom()
            val randomNumber = random.nextInt(9000) + 1000

            return "$timestamp$randomNumber"
        }

        fun getInitials(name: String?): String {
            var _name = name?.trim()

            if (_name.isNullOrEmpty()) {
                _name = "NA"
            }

            val result = runCatching {
                _name.split(" ").map {
                    it.first()
                }.joinToString("")
                    .padStart(2, ' ')
                    .substring(0, 2)
                    .trim()
                    .uppercase()
            }

            return result.getOrNull() ?: "NA"
        }
    }
}