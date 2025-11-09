package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.TEST_FAILURE_WINDOW_DESC
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class DSPluginConfigurable : Configurable {

    private lateinit var panel: JPanel
    private val checkboxMap = mutableMapOf<String, JCheckBox>()
    private lateinit var animateCheckbox: JCheckBox

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
       return SETTINGS_NAME
    }

    override fun createComponent(): JComponent {
        val settings = DSPersistentState.getInstance()
        panel = JPanel(BorderLayout(10,10))
        val content = JPanel()
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)

        val availableIcons = Icons.entries.toTypedArray()

        availableIcons.forEach { icon ->
            val checkBox = JCheckBox(icon.iconName)
            checkBox.horizontalAlignment = SwingConstants.LEFT
            checkBox.isSelected = icon.path in settings.iconPaths

            val iconLabel = JLabel(ImageIcon(javaClass.getResource(icon.path)))
            iconLabel.horizontalAlignment = SwingConstants.CENTER
            val row = JPanel(FlowLayout(FlowLayout.LEFT))
            row.add(iconLabel)
            row.add(checkBox)

            checkboxMap[icon.path] = checkBox
            content.add(row)
        }

        animateCheckbox = JCheckBox(TEST_FAILURE_WINDOW_DESC).apply {
            isSelected = settings.animateOnFailedBuild
        }
        val scroll = JBScrollPane(content)
        scroll.preferredSize = Dimension(400,250)

        panel.add(scroll, BorderLayout.CENTER)
        panel.add(animateCheckbox, BorderLayout.SOUTH)
        panel.accessibleContext.accessibleName = this.javaClass.simpleName
        return panel
    }

    override fun isModified(): Boolean {
        val settings = DSPersistentState.getInstance()
        val selectedNow = checkboxMap.filterValues { it.isSelected }.keys
        return settings.iconPaths != selectedNow.toSet() ||
                settings.animateOnFailedBuild != animateCheckbox.isSelected
    }

    override fun apply() {
        val settings = DSPersistentState.getInstance()
        settings.iconPaths = checkboxMap
            .filterValues { it.isSelected }
            .keys
            .toMutableSet()
        settings.animateOnFailedBuild = animateCheckbox.isSelected

        ApplicationManager.getApplication()
            .messageBus
            .syncPublisher(DSSettingsListener.TOPIC)
            .settingsChanged(settings)
    }

    override fun reset() {
        val settings = DSPersistentState.getInstance()
        checkboxMap.forEach { (name, box) ->
            box.isSelected = name in settings.iconPaths
        }
        animateCheckbox.isSelected = settings.animateOnFailedBuild
    }
}