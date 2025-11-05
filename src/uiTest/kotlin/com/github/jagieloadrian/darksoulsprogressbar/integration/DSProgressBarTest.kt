package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.CUSTOM_WIDGET_NAME
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.wait
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
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.imageio.ImageIO
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
                val image = progressBars.getScreenshot()
                val file = File("build/reports/progressBars.png")
                ImageIO.write(image, "png", file)
                println("Saved snapshot to: " + file.absolutePath)
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
                val image = tooltipInStatusBar.getScreenshot()
                val file = File("build/reports/tooltipInStatusBar.png")
                ImageIO.write(image, "png", file)
                println("Saved snapshot to: " + file.absolutePath)

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
                wait(10.seconds)
//                invokeAction("RunAnything")
//                waitFor(5.seconds) {
//                    findAll<TextFieldFixture>(byXpath("//div[@class='SearchTextField']")).isNotEmpty()
//                }
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

                val scrnshotPath = driver.takeScreenshot("/build/testfailure/screenshot-${LocalDateTime.now().toString()}.png")
                println("Screenshot path: $scrnshotPath")

//                val failureWindow = x(xQuery { byClass("JBPopup") })
                val failureWindowJlabel = x(xQuery { byClass("JLabel") })
                val failureWindowJpanel = x(xQuery { byClass("JPanel") })

                val image = failureWindowJlabel.getScreenshot()
                val file = File("build/reports/failureWindowlabel.png")
                ImageIO.write(image, "png", file)
                println("Saved snapshot to: " + file.absolutePath)

                val imagePanel = failureWindowJpanel.getScreenshot()
                val filePanel = File("build/reports/failureWindowPanel.png")
                ImageIO.write(imagePanel, "png", filePanel)
                println("Saved snapshot to: " + filePanel.absolutePath)
//                val failureWindow = x(xQuery { byAccessibleName("TestFailureWindow") })
//                failureWindow shouldNotBe null
//                failureWindow.isVisible() shouldBe true
//                failureWindow.isEnabled() shouldBe true
//
//                val failureWindowComponent = failureWindow.component
//
//                failureWindowComponent.isShowing() shouldBe true
//                failureWindowComponent.isFocusOwner() shouldBe true
//                failureWindowComponent.getClass().toString() shouldContain JProgressBar::class.java.toString()
            }
        }

    }
}