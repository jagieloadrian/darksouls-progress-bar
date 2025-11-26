package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.ui.DSPluginConfiguratorApi
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.JCheckBox
import javax.swing.JPanel

class DSPluginConfigurableTest {

    private val persistence = mockk<DSPersistentState>(relaxed = true)
    private val configPanel = mockk<DSPluginConfiguratorApi>()
    private val publisher = mockk<DSSettingsListener>(relaxed = true)
    private val application = mockk<Application>(relaxed = true)

    private lateinit var panelCheckBox1: JCheckBox
    private lateinit var panelCheckBox2: JCheckBox
    private lateinit var animationCheckBox: JCheckBox

    @BeforeEach
    fun setup() {
        mockkStatic(ApplicationManager::class)
        every { ApplicationManager.getApplication() } returns application
        every { application.messageBus.syncPublisher(DSSettingsListener.TOPIC) } returns publisher

        panelCheckBox1 = JCheckBox("Red circle")
        panelCheckBox2 = JCheckBox("Blue square")
        animationCheckBox = spyk(JCheckBox("Animate on failed build"))

        every {
            configPanel.getPanel(any(), persistence, any())
        } answers {
            val assignLambda = arg<(String, JCheckBox) -> Unit>(2)
            assignLambda("/icons/red.svg", panelCheckBox1)
            assignLambda("/icons/blue.svg", panelCheckBox2)

            ConfigPanelResult(panel = mockk<JPanel>(), animationCheckBox = animationCheckBox)
        }

        every { persistence.iconPaths } returns mutableSetOf()
        every { persistence.animateOnFailedBuild } returns false
    }

    @Test
    fun `getDisplayName returns SETTINGS_NAME`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.displayName shouldBe SETTINGS_NAME
    }

    @Test
    fun `createComponent calls getPanel and registers animation listener`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)

        configurable.createComponent()

        verify { configPanel.getPanel(any(), persistence, any()) }

        animationCheckBox.actionListeners shouldHaveSize 1
    }

    @Test
    fun `isModified returns true when icon checkbox is toggled`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.createComponent()

        panelCheckBox1.isSelected = true

        configurable.isModified() shouldBe true
    }

    @Test
    fun `reset restores icon checkboxes and animation state from persistence`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.createComponent()

        panelCheckBox1.isSelected = true
        panelCheckBox2.isSelected = false
        animationCheckBox.isSelected = false

        every { persistence.iconPaths } returns mutableSetOf("/icons/blue.svg")
        every { persistence.animateOnFailedBuild } returns true

        configurable.reset()

        panelCheckBox1.isSelected shouldBe false
        panelCheckBox2.isSelected shouldBe true
        animationCheckBox.isSelected shouldBe true
    }
}