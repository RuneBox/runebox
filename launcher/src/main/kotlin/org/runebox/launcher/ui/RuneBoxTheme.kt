package org.runebox.launcher.ui

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.IntelliJTheme

class RuneBoxTheme : IntelliJTheme.ThemeLaf(IntelliJTheme(RuneBoxTheme::class.java.getResourceAsStream("/theme.json"))) {
    companion object {
        fun setup() {
            FlatLaf.setup(RuneBoxTheme())
        }
    }
}