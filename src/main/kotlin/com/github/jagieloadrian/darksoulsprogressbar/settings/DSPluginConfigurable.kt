package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.model.Icons
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.TEST_FAILURE_WINDOW_DESC
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

class DSPluginConfigurable(
    private val configPanel: DSPluginConfiguratorApi = DSPluginConfiguratorUI,
    private val persistence: DSPersistentState = DSPersistentState.getInstance(),
) : Configurable {

    private val checkboxMap = mutableMapOf<String, JCheckBox>()
    private var animationStatus = persistence.animateOnFailedBuild
    private lateinit var animationCheckBox: JCheckBox


    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return SETTINGS_NAME
    }

    override fun createComponent(): JComponent {
        val result = configPanel.getPanel(animationStatus, persistence) { path, checkBox ->
            checkboxMap[path] = checkBox
        }

        animationCheckBox = result.animationCheckBox.also {
            it.addActionListener {
                animationStatus = animationCheckBox.isSelected
            }
        }
        return result.panel
    }

    override fun isModified(): Boolean {
        val selectedNow = checkboxMap.filterValues { it.isSelected }.keys
        return persistence.iconPaths != selectedNow.toSet() ||
                persistence.animateOnFailedBuild != animationStatus
    }

    override fun apply() {
        persistence.iconPaths = checkboxMap
            .filterValues { it.isSelected }
            .keys
            .toMutableSet()
        persistence.animateOnFailedBuild = animationStatus

        ApplicationManager.getApplication()
            .messageBus
            .syncPublisher(DSSettingsListener.TOPIC)
            .settingsChanged(persistence)
    }

    override fun reset() {
        checkboxMap.forEach { (name, box) ->
            box.isSelected = name in persistence.iconPaths
        }
        animationStatus = persistence.animateOnFailedBuild
        if (::animationCheckBox.isInitialized) {
            animationCheckBox.isSelected = animationStatus
        }
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        if (::animationCheckBox.isInitialized) {
            animationCheckBox.removeActionListener { }
        }
    }
}

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

data class ConfigPanelResult(
    val panel: JPanel,
    val animationCheckBox: JCheckBox,
)