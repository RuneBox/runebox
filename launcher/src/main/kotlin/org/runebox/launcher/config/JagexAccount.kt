package org.runebox.launcher.config

import kotlinx.serialization.Serializable

@Serializable
data class JagexAccount(
    val name: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val sessionId: String,
    val characters: List<Character>
) {
}

class MyClassLoader : 