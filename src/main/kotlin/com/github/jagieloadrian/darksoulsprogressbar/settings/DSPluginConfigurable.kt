package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.ui.DSPluginConfiguratorApi
import com.github.jagieloadrian.darksoulsprogressbar.ui.DSPluginConfiguratorUI
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

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

data class ConfigPanelResult(
    val panel: JPanel,
    val animationCheckBox: JCheckBox,
)