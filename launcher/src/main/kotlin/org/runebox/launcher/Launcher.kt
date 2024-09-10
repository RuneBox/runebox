package org.runebox.launcher

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.runebox.common.Dirs
import org.runebox.launcher.config.LauncherConfig
import org.runebox.launcher.ui.LauncherFrame
import org.runebox.launcher.ui.RuneBoxTheme
import org.tinylog.kotlin.Logger

object Launcher {

    lateinit var config : LauncherConfig
    lateinit var frame: LauncherFrame

    @JvmStatic
    fun main(args: Array<String>) {
        start()
    }

    fun start() {
        Logger.info("Preparing to start RuneBox launcher")

        // Initialize Dirs
        initDirs()

        // Initialize Config
        initConfig()

        // Initialize LAF Theme
        RuneBoxTheme.setup()

        // Open launcher window
        frame = LauncherFrame()
        frame.open()
    }

    private fun initDirs() {
        Logger.info("Initializing directories...")
        for(dir in Dirs.DIRS_LIST) {
            if(!dir.exists()) {
                Logger.info("Missing required directory: ${dir.name}.")
                dir.mkdirs()
            }
        }
    }

    private fun initConfig() {
        Logger.info("Initializing configuration...")
        config = LauncherConfig.load()
        config.save()
    }
}
