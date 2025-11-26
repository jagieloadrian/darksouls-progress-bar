package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.ui.TestFailureWindowApi
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DSFailingProcessHandlerTest {

    private val application = mockk<Application>(relaxed = true)

    @BeforeEach
    fun setup() {
        mockkStatic(ApplicationManager::class)
        every { ApplicationManager.getApplication() } returns application
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(ApplicationManager::class)
    }

    @Test
    fun `should show window when exitCode != 0 and executor Run`() {
        val window = StubTestFailureWindow()
        val handler = DSFailingProcessHandler(window)

        handler.shouldShowWindow(-1, "Run") shouldBe true
    }

    @Test
    fun `should NOT show failure window for exitCode 0`() {
        val window = StubTestFailureWindow()
        val handler = DSFailingProcessHandler(window)

        handler.processTerminated(
            executorId = "Run",
            env = mockk(),
            handler = mockk(),
            exitCode = 0
        )

        window.showCalled shouldBe false
    }

    @Test
    fun `should NOT show failure window when executor is not Run`() {
        val window = StubTestFailureWindow()
        val handler = DSFailingProcessHandler(window)

        handler.processTerminated(
            executorId = "Debug",
            env = mockk(),
            handler = mockk(),
            exitCode = -1
        )

        window.showCalled shouldBe false
    }
}

class StubTestFailureWindow : TestFailureWindowApi {
    var showCalled = false
    override fun show() {
        showCalled = true
    }
}