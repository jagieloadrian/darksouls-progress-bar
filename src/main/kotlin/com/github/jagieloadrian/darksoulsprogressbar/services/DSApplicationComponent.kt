package com.github.jagieloadrian.darksoulsprogressbar.services

import com.github.jagieloadrian.darksoulsprogressbar.ui.DSProgressBarUI
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.wm.IdeFrame
import javax.swing.UIManager

class DSApplicationComponent : LafManagerListener, ApplicationActivationListener, DynamicPluginListener {

    override fun lookAndFeelChanged(p0: LafManager) {
        updateProgressBarUI()
    }

    override fun applicationActivated(ideFrame: IdeFrame) {
        updateProgressBarUI()
    }

    private fun updateProgressBarUI() {
        UIManager.put("ProgressBarUI", DSProgressBarUI::class.java.name)
        UIManager.getDefaults()[DSProgressBarUI::class.java.name] = DSProgressBarUI::class.java
    }

    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        super.pluginLoaded(pluginDescriptor)
        updateProgressBarUI()
    }
}