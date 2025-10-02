package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.intellij.openapi.diagnostic.DefaultLogger
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.Timer

object TestFailureOverlay {
    private var popup: JBPopup? = null
    private val logger = DefaultLogger.getInstance(this::class.java)

    fun show() {
        logger.info("Wszedłem do TestFailureOverlay i popup jest: $popup")
        if (popup != null) return

        val panel = JPanel()
        panel.preferredSize = Dimension(480, 268)
        val label = JLabel("Test Failed!", SwingConstants.CENTER)

        label.icon = ImageIcon(javaClass.getResource("/gif/you_died.gif"))
        panel.add(label)

        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle("Test Failure")
            .setResizable(false)
            .setMovable(false)
            .setRequestFocus(true)
            .createPopup()

        popup?.showInFocusCenter()

        Timer(3000) { this.hide() }
    }

    fun hide() {
        popup?.cancel()
        popup = null
    }
}