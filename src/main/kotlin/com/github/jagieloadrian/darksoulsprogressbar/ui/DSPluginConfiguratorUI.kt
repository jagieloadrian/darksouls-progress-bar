package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import com.github.jagieloadrian.darksoulsprogressbar.settings.ConfigPanelResult
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.TEST_FAILURE_WINDOW_DESC
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

interface DSPluginConfiguratorApi {
    fun getPanel(
        animationEnabled: Boolean,
        settings: DSPersistentState,
        assignCheckBoxToPath: (String, JCheckBox) -> Unit,
    ): ConfigPanelResult
}

object DSPluginConfiguratorUI : DSPluginConfiguratorApi {
    override fun getPanel(
        animationEnabled: Boolean,
        settings: DSPersistentState,
        assignCheckBoxToPath: (String, JCheckBox) -> Unit,
    ): ConfigPanelResult {

        val panel = JPanel(BorderLayout(10, 10))
        val content = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        Icons.entries.forEach { icon ->
            val checkBox = JCheckBox(icon.iconName).apply {
                horizontalAlignment = SwingConstants.LEFT
                isSelected = icon.path in settings.iconPaths
            }

            val iconLabel = JLabel(ImageIcon(javaClass.getResource(icon.path))).apply {
                horizontalAlignment = SwingConstants.CENTER
            }

            JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(iconLabel)
                add(checkBox)
                content.add(this)
            }

            assignCheckBoxToPath(icon.path, checkBox)
        }

        val animationCheckBox = JCheckBox(TEST_FAILURE_WINDOW_DESC).apply {
            isSelected = animationEnabled
        }

        val scroll = JBScrollPane(content).apply {
            preferredSize = Dimension(400, 250)
        }

        panel.add(scroll, BorderLayout.CENTER)
        panel.add(animationCheckBox, BorderLayout.SOUTH)
        panel.accessibleContext.accessibleName = "DSPluginConfigPanel"

        return ConfigPanelResult(panel, animationCheckBox)
    }

}