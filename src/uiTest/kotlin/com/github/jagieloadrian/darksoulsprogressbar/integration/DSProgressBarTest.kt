package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.CUSTOM_WIDGET_NAME
import com.intellij.driver.sdk.ui.components.ideFrame
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.wait
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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

@Tag("ui")
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
                    .withVersion("2024.3")
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
    fun `check if the status bar has custom widget`() {
        run.driver.withContext {
            ideFrame {
                wait(1.minutes)
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

//    @Test
//    fun `check if the progress bar exist`() {
//        run.driver.withContext {
//            ideFrame {
//
//            }
//        }
//    }
}