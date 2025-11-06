package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.CUSTOM_WIDGET_NAME
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.waitFor
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.driver.engine.BackgroundRun
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import com.intellij.util.applyIf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.awt.event.KeyEvent.VK_CONTROL
import java.nio.file.Paths
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Tag("ui")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DSProgressBarTest {
    companion object {
        private lateinit var run: BackgroundRun
        private lateinit var project: LocalProjectInfo

        @BeforeAll
        @JvmStatic
        fun `loading project into ide`() {
            project = LocalProjectInfo(
                Path("src/uiTest/resources/project/WebSocketEcho"),
                isReusable = true, description = "Simple kotlin project with ktor"
            )
            run = Starter.newContext(
                "Test Context",
                TestCase(IdeProductProvider.IC, projectInfo = project)
                    .withVersion("2025.2")
            ).applyIf(true) {
                val pluginPath = System.getProperty("path.to.build.plugin")
                PluginConfigurator(this).installPluginFromPath(Paths.get(pluginPath))
                this
            }.runIdeWithDriver()
        }

        @AfterAll
        @JvmStatic
        fun `closing ide after test`() {
            run.closeIdeAndWait()
        }

    }

    @Test
    @Order(1)
    fun `check if the progress bar exist`() {
        run.driver.withContext {
            ideFrame {
                waitForProjectOpen(1.minutes)
                val progressBars = x(xQuery { byAccessibleName("DSProgressBarUI") })

                progressBars.isVisible() shouldBe true
                progressBars.isEnabled() shouldBe true
                progressBars.component.getClass().toString() shouldContain JProgressBar::class.java.toString()
                progressBars.component.isShowing() shouldBe true
            }
        }
    }

    @Test
    @Order(2)
    fun `check if the status bar has custom widget`() {
        run.driver.withContext {
            ideFrame {
                val tooltipInStatusBar = x(xQuery { byTooltip(CUSTOM_WIDGET_NAME) })
                tooltipInStatusBar shouldNotBe null
                val getSomeRestComponent = tooltipInStatusBar.component
                getSomeRestComponent.isEnabled() shouldBe true
                getSomeRestComponent.isShowing() shouldBe true
                getSomeRestComponent.isDisplayable() shouldBe true
                getSomeRestComponent.getAccessibleContext()
            }
        }
    }

    @Test
    @Order(3)
    fun `should show screen with failing message`() {
        run.driver.withContext {
            ideFrame {
                keyboard {
                    doublePressing(VK_CONTROL) {}
                    enterText("gradle clean build")
                    enter()
                }
                waitFor(timeout = 30.seconds) {
                    val tree = x(xQuery { byClass("Tree") })
                    tree.present() &&
                            tree.allTextAsString().contains("[clean build]: failed")
                }

                val failureWindow = x(xQuery { byAccessibleName("TestFailureWindow") })
                failureWindow shouldNotBe null
                failureWindow.isVisible() shouldBe true
                failureWindow.isEnabled() shouldBe true

                val failureWindowComponent = failureWindow.component

                failureWindowComponent.isShowing() shouldBe true
                failureWindowComponent.isFocusOwner() shouldBe true
                failureWindowComponent.getClass().toString() shouldContain JPanel::class.java.toString()
            }
        }
    }
}