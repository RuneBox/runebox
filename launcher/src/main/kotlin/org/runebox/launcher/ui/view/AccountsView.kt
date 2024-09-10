package org.runebox.launcher.ui.view

import de.jensd.fx.glyphs.GlyphsDude
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.css.StyleClass
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.runebox.launcher.Launcher
import org.runebox.launcher.config.LauncherConfig
import tornadofx.*
import tornadofx.FX.Companion.icon

class AccountsView : View() {

    override var root = vbox {}

    fun reload() {
        Launcher.config = LauncherConfig.load()
        Launcher.config.save()

        root.children.clear()
        root = vbox {
            vgrow = Priority.ALWAYS
            alignment = Pos.TOP_CENTER
            paddingTop = 35.0

            val accounts = Launcher.config.accounts
            if(accounts.isEmpty()) {
                label("No Accounts Found") {
                    font = Font.font(20.0)
                }
            } else {
                squeezebox {
                    spacing = 30.0
                    paddingTop = 35.0
                    for(account in accounts) {
                        fold(account.name, expanded = true, closeable = true) {
                            prefWidth = 275.0
                            for(character in account.characters) {
                                vbox {
                                    spacing = 3.0
                                    hbox {
                                        spacing = 5.0
                                        val charName = if(character.displayName.isNotBlank()) character.displayName else "Name Not Set"
                                        label(charName)
                                        hbox {
                                            hgrow = Priority.ALWAYS
                                            alignment = Pos.CENTER_RIGHT
                                            button {
                                                setPrefSize(16.0, 16.0)
                                                GlyphsDude.createIcon(FontAwesomeIcon.PLAY, "16px").also { icon ->
                                                    icon.styleClass.add("run-icon")
                                                    icon.fill = Color.rgb(73, 156, 84)
                                                    add(icon)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        reload()
    }
}