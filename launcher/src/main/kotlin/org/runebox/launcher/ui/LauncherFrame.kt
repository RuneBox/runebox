package org.runebox.launcher.ui

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.stage.Stage
import org.runebox.launcher.ui.view.RootView
import tornadofx.FX
import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.SwingUtilities

class LauncherFrame : JFrame("RuneBox Launcher") {

    lateinit var wrapper: JFXPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(400, 550)
        preferredSize = size
        setLocationRelativeTo(null)
        isResizable = false
        iconImages = ICON_IMAGES
    }

    fun open() {
        SwingUtilities.invokeLater {
            wrapper = JFXPanel()
            wrapper.scene = Scene(FX.find<RootView>().root)
            LauncherFrame::class.java.getResource("/theme.css")?.toExternalForm()?.also {
                wrapper.scene.stylesheets.add(it)
            }
            contentPane.add(wrapper)
            isVisible = true
            Platform.runLater {
                val stage = Stage()
                val app = LauncherApp()
                app.start(stage)
                //ScenicView.show(wrapper.scene)
            }
        }
    }

    private companion object {
        val ICON_IMAGES = listOf(
            "icon_1024.png",
            "icon_512.png",
            "icon_256.png",
            "icon_128.png"
        ).map { ImageIcon(LauncherFrame::class.java.getResource("/images/$it")).image }
    }
}