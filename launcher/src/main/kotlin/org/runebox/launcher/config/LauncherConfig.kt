package org.runebox.launcher.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.runebox.common.Dirs

@Serializable
data class LauncherConfig(
    val accounts: MutableList<JagexAccount>
) {
    fun save() {
        val file = Dirs.CONFIGS_DIR.resolve("launcher.conf")
        if(file.exists()) {
            file.delete()
        }
        file.createNewFile()
        file.writeText(Json.encodeToString(this))
    }

    companion object {
        fun load(): LauncherConfig {
            val config = LauncherConfig(mutableListOf())
            val file = Dirs.CONFIGS_DIR.resolve("launcher.conf")
            return if(!file.exists()) {
                config
            } else {
                Json.decodeFromString<LauncherConfig>(file.readText())
            }
        }
    }
}