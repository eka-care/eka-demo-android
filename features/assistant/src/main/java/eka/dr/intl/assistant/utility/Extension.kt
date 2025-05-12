package eka.dr.intl.assistant.utility

object Extension {
    fun String.isNumeric(): Boolean {
        return this.matches(Regex("\\d+"))
    }
    fun String.isOnlyLetter(): Boolean {
        return this.all { it.isLetter() || it.isWhitespace()|| it in setOf('.', '%', ',', '*',';','@','&','!','#','$','(',')') }

    }

    fun String.capitalizeFirstWordOfEachString(): String {
        val words = this.split(" ")
        val capitalizedWords = words.mapIndexed { index, word ->
            if (index == 0) {
                word.capitalize()
            } else {
                word
            }
        }
        return capitalizedWords.joinToString(" ")
    }

}