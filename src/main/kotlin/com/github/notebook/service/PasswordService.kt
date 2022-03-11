package com.github.notebook.service

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.util.*

object PasswordService {
    private const val cost = 10
    private val hashser = BCrypt.with(BCrypt.Version.VERSION_2B)
    private val verifyer = BCrypt.verifyer()

    fun hashPassword(plainTextPassword: String): String = hashser.hashToString(cost, plainTextPassword.toCharArray())

    fun verifyPassword(plainTextPassword: String, hashedPassword: String): Boolean =
        verifyer.verify(plainTextPassword.toCharArray(), hashedPassword).verified
}