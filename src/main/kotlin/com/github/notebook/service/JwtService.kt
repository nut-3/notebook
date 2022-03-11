package com.github.notebook.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import java.util.*
import kotlin.properties.Delegates


object JwtService {

    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private lateinit var myRealm: String
    private var expire by Delegates.notNull<Long>()

    fun generateJwt(userName: String, roles: List<String>): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", userName)
        .withClaim("roles", roles)
        .withExpiresAt(Date(System.currentTimeMillis() + expire * 1000))
        .sign(Algorithm.HMAC256(secret))

    fun getVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun setConfig(config: ApplicationConfig) {
        issuer = config.property("jwt.issuer").getString()
        audience = config.property("jwt.audience").getString()
        myRealm = config.property("jwt.realm").getString()
        expire = config.property("jwt.expire").getString().toLong()
        secret = config.property("jwt.secret").getString()
    }
}