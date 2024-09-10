package org.runebox.launcher.ui.controller

import org.runebox.launcher.auth.JagexAuth
import org.tinylog.kotlin.Logger
import tornadofx.Controller
import javax.swing.SwingUtilities

class RootController : Controller() {

    private var jagexAuth = JagexAuth()

    fun addJagexAccount() {
        Logger.info("Opening Jagex account addition window")
        jagexAuth.openLoginPage()
    }
}