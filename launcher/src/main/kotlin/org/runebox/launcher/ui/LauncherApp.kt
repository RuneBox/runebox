package org.runebox.launcher.ui

import javafx.stage.Stage
import org.runebox.launcher.ui.view.RootView
import tornadofx.App
import tornadofx.FX
import tornadofx.importStylesheet

class LauncherApp : App(RootView::class) {

    init {
        importStylesheet("theme.css")
    }

    override fun start(stage: Stage) {
        FX.registerApplication(this, stage)
    }
}