package org.runebox.common

import java.io.File

object Dirs {

    val BASE_DIR = File(System.getProperty("user.home")).resolve(".runebox/")
    val LOGS_DIR = BASE_DIR.resolve("logs/")
    val PLUGINS_DIR = BASE_DIR.resolve("plugins/")
    val BIN_DIR = BASE_DIR.resolve("bin/")
    val CACHE_DIR = BASE_DIR.resolve("cache/")
    val CONFIGS_DIR = BASE_DIR.resolve("configs/")

    val DIRS_LIST = listOf(
        BASE_DIR,
        LOGS_DIR,
        PLUGINS_DIR,
        BIN_DIR,
        CACHE_DIR,
        CONFIGS_DIR,
    )
}