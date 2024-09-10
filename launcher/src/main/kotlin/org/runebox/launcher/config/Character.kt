package org.runebox.launcher.config

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val displayName: String,
    val characterId: String,
    val userHash: String,
) {
}