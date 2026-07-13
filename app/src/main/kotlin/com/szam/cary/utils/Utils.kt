package com.szam.cary.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale("id", "ID"))

    fun today(): String = LocalDate.now().format(formatter)

    fun toDisplayFormat(dateStr: String): String {
        return try {
            LocalDate.parse(dateStr, formatter).format(displayFormatter)
        } catch (e: DateTimeParseException) {
            dateStr
        }
    }
}

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

// Sanitasi input
fun isSafeInput(input: String): Boolean {
    val forbidden = Regex("[<>\"'%;()&+]")
    return !forbidden.containsMatchIn(input)
}

// Password policy NIST
fun validatePassword(password: String): String? {
    if (password.length < 8) return "Password minimal 8 karakter"
    if (password in listOf("password", "12345678", "11111111"))
        return "Password terlalu umum"
    return null
}