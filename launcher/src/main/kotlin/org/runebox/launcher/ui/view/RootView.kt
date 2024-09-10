package org.runebox.launcher.ui.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.runebox.launcher.ui.controller.RootController
import tornadofx.*

class RootView : View("RuneBox Launcher") {

    private val rootController: RootController by inject()

    override val root = borderpane {
        setPrefSize(450.0, 600.0)
        center {
            hbox {
                prefWidth = 350.0
                usePrefWidth = true
                alignment = Pos.CENTER
                add(find<AccountsView>())
            }
        }
        bottom {
            hbox {
                alignment = Pos.CENTER
                button("Add Jagex Account") {
                    minWidth = 225.0
                    minHeight = 35.0
                    action { Platform.runLater { rootController.addJagexAccount() } }
                    style += """
                        -fx-base: #feb720;
                    """.trimIndent()
                }
            }
            paddingBottom = 10.0
        }
    }
}