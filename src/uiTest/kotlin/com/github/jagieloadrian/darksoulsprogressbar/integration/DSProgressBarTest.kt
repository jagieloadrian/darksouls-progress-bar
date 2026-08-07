package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.CUSTOM_WIDGET_NAME
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.TEST_FAILURE_WINDOW_NAME
import com.intellij.driver.sdk.ui.components.UiComponent
import com.intellij.driver.sdk.ui.components.common.dialogs.editRunConfigurationsDialog
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.common.popups.runConfigurationsList
import com.intellij.driver.sdk.ui.components.common.popups.runConfigurationsPopup
import com.intellij.driver.sdk.ui.components.elements.tree
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
import io.mockk.InternalPlatformDsl.toStr
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.nio.file.Paths
import javax.swing.JLabel
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
                TestCase(IdeProductProvider.IU, projectInfo = project)
                    .withVersion("2025.3")
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

                progressBars.component.isShowing() shouldBe true
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
                waitForProjectOpen(1.minutes)
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
                waitForProjectOpen(1.minutes)
                waitForIndicators(3.minutes)
                // ponytail: "alwaysFail" never ran before in a fresh sandbox, so the redesigned run widget's
                // quick popup only shows recents/"Current File"/"Edit Configurations…" - go through the dialog instead
                runConfigurationsPopup {
                    runConfigurationsList {
                        clickItem("Edit Configurations…")
                    }
                }
                editRunConfigurationsDialog {
                    // ponytail: fresh sandbox has no persisted tree UI state, so the config type
                    // nodes render collapsed and "alwaysFail" isn't in the accessible tree yet.
                    // Default expandAll() timeout (5s) is too tight while CI's 2-core runner is
                    // also churning through a cold nested Gradle sync at the same time.
                    tree().expandAll(30.seconds)
                    x(xQuery { byVisibleText("alwaysFail") }).click()
                    runButton.click()
                }

                lateinit var failureWindow: UiComponent
                waitFor(timeout = 1.minutes) {
                    failureWindow = x(xQuery { byAccessibleName(TEST_FAILURE_WINDOW_NAME) })
                    failureWindow.present()
                }

                failureWindow shouldNotBe null
                failureWindow.component.isShowing() shouldBe true
                failureWindow.isEnabled() shouldBe true
                val failureWindowComponent = failureWindow.component

                failureWindowComponent.toStr() shouldContain Items.YOU_DIED_GIF
                failureWindowComponent.isShowing() shouldBe true
                failureWindowComponent.getClass().toString() shouldContain JLabel::class.java.toString()
            }
        }
    }
}