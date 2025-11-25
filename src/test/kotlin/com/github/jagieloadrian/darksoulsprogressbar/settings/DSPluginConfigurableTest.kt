package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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

    // checkboxes in stub panel
    private lateinit var panelCheckBox1: JCheckBox
    private lateinit var panelCheckBox2: JCheckBox
    private lateinit var animationCheckBox: JCheckBox

    @BeforeEach
    fun setup() {
        mockkStatic(ApplicationManager::class)
        every { ApplicationManager.getApplication() } returns application
        every { application.messageBus.syncPublisher(DSSettingsListener.TOPIC) } returns publisher

        // przygotowanie checkboxów do mapy w panelu
        animationCheckBox = JCheckBox("Animate")
        panelCheckBox1 = JCheckBox("icon1")
        panelCheckBox2 = JCheckBox("icon2")

        // stub panelu, wywołuje lambdę, żeby wypełnić checkboxMap
        every {
            configPanel.getPanel(any(), persistence, any())
        } answers {
            val lambda = arg<(String, JCheckBox) -> Unit>(2)
            lambda("icon1", panelCheckBox1)
            lambda("icon2", panelCheckBox2)
            mockk<JPanel>()
        }
    }

    @Test
    fun `getDisplayName returns SETTINGS_NAME`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.displayName shouldBe SETTINGS_NAME
    }

    @Test
    fun `createComponent returns JComponent`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        val component = configurable.createComponent()
        component.shouldNotBeNull()
        verify { configPanel.getPanel(any(), persistence, any()) }
    }

    @Test
    fun `isModified returns true when iconPaths changed`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.createComponent()

        // simulation click on checkbox1
        panelCheckBox1.isSelected = true
        panelCheckBox2.isSelected = false
        every { persistence.iconPaths } returns mutableSetOf()
        every { persistence.animateOnFailedBuild } returns false

        configurable.isModified() shouldBe true
    }

    @Test
    fun `reset restores checkboxes from persistence`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.createComponent()

        every { persistence.iconPaths } returns mutableSetOf("icon2")
        every { persistence.animateOnFailedBuild } returns true

        // ustawiamy losowe stany
        panelCheckBox1.isSelected = true
        panelCheckBox2.isSelected = false
        animationCheckBox.isSelected = false

        configurable.reset()

        panelCheckBox1.isSelected shouldBe false
        panelCheckBox2.isSelected shouldBe true
        animationCheckBox.isSelected shouldBe false
    }

    @Test
    fun `isModified returns true when animationCheckBox changed`() {
        val configurable = DSPluginConfigurable(configPanel, persistence)
        configurable.createComponent()

        // symulujemy zmianę stanu animacji
        animationCheckBox.isSelected = !persistence.animateOnFailedBuild
        every { persistence.iconPaths } returns mutableSetOf()
        every { persistence.animateOnFailedBuild } returns true

        configurable.isModified() shouldBe true
    }
}