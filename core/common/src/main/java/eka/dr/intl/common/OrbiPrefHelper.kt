package eka.dr.intl.common

import android.content.SharedPreferences
import android.util.Log

object OrbiPrefHelper {

    @JvmStatic
    @Throws
    fun <T> setValue(sharedPreferences: SharedPreferences, key: String, value: T?) {
        setValueInternal(
            sharedPreferences,
            key,
            value
        ).apply()
    }

    @JvmStatic
    @Throws
    fun <T> setValueCommit(sharedPreferences: SharedPreferences, key: String, value: T?): Boolean {
        return setValueInternal(
            sharedPreferences,
            key,
            value
        ).commit()
    }

    fun clearPref(sharedPreferences: SharedPreferences) {
        try {
            sharedPreferences.edit().clear().apply()
        } catch (e: Exception) {
            Log.e("OrbiPrefHelper", "Error clearing preferences: ${e.message}")
        }
    }


    @JvmStatic
    @Throws
    private fun <T> setValueInternal(
        sharedPreferences: SharedPreferences,
        key: String,
        value: T?
    ): SharedPreferences.Editor {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> {
                editor.putString(key, value)
            }

            is Boolean -> {
                editor.putBoolean(key, value)
            }

            is Long -> {
                editor.putLong(key, value)
            }

            is Int -> {
                editor.putInt(key, value)
            }

            is Float -> {
                editor.putFloat(key, value)
            }

            null -> {
                editor.putString(key, value)
            }

            else -> {
                throw ClassCastException("Unknown class of value value")
            }
        }
        return editor
    }

    @JvmStatic
    @Throws
    fun <T> getValue(sharedPreferences: SharedPreferences, key: String, default: T?): T? {
        return when (default) {
            is String -> {
                fetchStringValue<T>(
                    sharedPreferences,
                    key,
                    default
                )
            }

            is Boolean -> {
                sharedPreferences.getBoolean(key, default) as T?
            }

            is Long -> {
                fetchLongValue<T>(
                    sharedPreferences,
                    key,
                    default
                )
            }

            is Int -> {
                fetchIntValue<T>(
                    sharedPreferences,
                    key,
                    default
                )
            }

            is Float -> {
                sharedPreferences.getFloat(key, default) as T?
            }

            null -> {
                fetchStringValue<T>(
                    sharedPreferences,
                    key,
                    null
                )
            }

            else -> {
                throw ClassCastException("Unknown class of default value")
            }
        }
    }

    private fun <T> fetchIntValue(
        sharedPreferences: SharedPreferences,
        key: String,
        default: T?
    ): T? {
        return try {
            sharedPreferences.getInt(key, default as Int) as T?
        } catch (ce: ClassCastException) {
            try {
                (longTry(sharedPreferences, key)
                    ?.toInt() as T?) ?: default
            } catch (ee: Exception) {
                default
            }
        } catch (e: Exception) {
            default
        }
    }

    private fun <T> fetchLongValue(
        sharedPreferences: SharedPreferences,
        key: String,
        default: T?
    ): T? {
        return try {
            sharedPreferences.getLong(key, default as Long) as T?
        } catch (ce: ClassCastException) {
            try {
                (intTry(sharedPreferences, key)?.toLong() as T?) ?: default
            } catch (ee: Exception) {
                default
            }
        } catch (e: Exception) {
            default
        }
    }

    private fun <T> fetchStringValue(
        sharedPreferences: SharedPreferences,
        key: String,
        default: T?
    ): T? {
        return try {
            sharedPreferences.getString(key, default as String?) as T?
        } catch (ce: ClassCastException) {
            try {
                (intTry(sharedPreferences, key)?.toString() as T?) ?: default
            } catch (sice: ClassCastException) {
                try {
                    (longTry(
                        sharedPreferences,
                        key
                    )?.toString() as T?) ?: default
                } catch (lce: ClassCastException) {
                    try {
                        (floatTry(
                            sharedPreferences,
                            key
                        )?.toString() as T?) ?: default
                    } catch (fce: ClassCastException) {
                        try {
                            (booleanTry(
                                sharedPreferences,
                                key,
                                default as Boolean
                            ) as T?)
                                ?: default
                        } catch (e: Exception) {
                            default
                        }
                    }
                }
            }
        }
    }

    @Throws
    private fun intTry(sharedPreferences: SharedPreferences, key: String): Int? {
        val intValue = sharedPreferences.getInt(key, Integer.MIN_VALUE)
        return if (intValue == Integer.MIN_VALUE) {
            null
        } else {
            intValue
        }
    }

    @Throws
    private fun longTry(sharedPreferences: SharedPreferences, key: String): Long? {
        val longValue = sharedPreferences.getLong(key, Long.MIN_VALUE)
        return if (longValue == Long.MIN_VALUE) {
            null
        } else {
            longValue
        }
    }

    @Throws
    private fun floatTry(sharedPreferences: SharedPreferences, key: String): Float? {
        val floatValue = sharedPreferences.getFloat(key, Float.MIN_VALUE)
        return if (floatValue == Float.MIN_VALUE) {
            null
        } else {
            floatValue
        }
    }

    @Throws
    private fun booleanTry(
        sharedPreferences: SharedPreferences,
        key: String,
        default: Boolean
    ): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }
}