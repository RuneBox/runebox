package org.runebox.launcher.auth

data class AccountToken(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String,
    val scope: String,
    val tokenType: String,
    val expiresIn: Int = -1
)