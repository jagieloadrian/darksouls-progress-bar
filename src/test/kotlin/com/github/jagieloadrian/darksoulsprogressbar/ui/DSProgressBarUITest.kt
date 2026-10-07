package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSSettingsListener
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import javax.swing.JProgressBar

class DSProgressBarUITest {

    private val state = DSPersistentState()
    private var listener: DSSettingsListener? = null
    private var disconnected = false
    private val connection = mock(MessageBusConnection::class.java) { call ->
        when (call.method.name) {
            "subscribe" -> listener = call.getArgument(1)
            "disconnect" -> disconnected = true
        }
        null
    }
    private lateinit var applicationManager: MockedStatic<ApplicationManager>

    @BeforeEach
    fun setUp() {
        System.setProperty("java.awt.headless", "true")
        val bus = mock(MessageBus::class.java)
        `when`(bus.connect()).thenReturn(connection)
        val app = mock(Application::class.java)
        `when`(app.messageBus).thenReturn(bus)
        `when`(app.getService(DSPersistentState::class.java)).thenReturn(state)
        applicationManager = mockStatic(ApplicationManager::class.java)
        applicationManager.`when`<Application> { ApplicationManager.getApplication() }.thenReturn(app)
    }

    @AfterEach
    fun tearDown() = applicationManager.close()

    @Test
    fun `empty icon selection does not throw`() {
        state.iconPaths = mutableSetOf()

        shouldNotThrowAny { DSProgressBarUI() }
    }

    @Test
    fun `settings change with empty selection does not throw`() {
        DSProgressBarUI()

        shouldNotThrowAny {
            listener!!.settingsChanged(DSPersistentState().apply { iconPaths = mutableSetOf() })
        }
    }

    @Test
    fun `uninstallUI disconnects message bus listener`() {
        val ui = DSProgressBarUI()
        val bar = JProgressBar()
        ui.installUI(bar)

        ui.uninstallUI(bar)

        disconnected shouldBe true
    }
}
