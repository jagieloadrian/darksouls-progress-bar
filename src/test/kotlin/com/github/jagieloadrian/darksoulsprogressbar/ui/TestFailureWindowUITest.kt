package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class TestFailureWindowUITest {

    @Test
    fun `show does nothing when animation is disabled`() {
        val state = DSPersistentState().apply { animateOnFailedBuild = false }
        val bus = mockk<MessageBus> {
            every { connect(any<Disposable>()) } returns mockk<MessageBusConnection>(relaxed = true)
        }
        val app = mockk<Application> {
            every { messageBus } returns bus
            every { getService(DSPersistentState::class.java) } returns state
        }
        Mockito.mockStatic(ApplicationManager::class.java).use {
            it.`when`<Application> { ApplicationManager.getApplication() }.thenReturn(app)
            shouldNotThrowAny { TestFailureWindowUI.show() }
        }
    }
}
