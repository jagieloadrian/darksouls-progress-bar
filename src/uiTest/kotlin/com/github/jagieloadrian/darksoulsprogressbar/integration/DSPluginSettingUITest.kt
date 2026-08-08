package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.DS_PLUGIN_CONFIGURABLE_NAME
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.SETTINGS_NAME
import com.intellij.driver.sdk.ui.Finder
import com.intellij.driver.sdk.ui.UiText.Companion.asString
import com.intellij.driver.sdk.ui.components.UiComponent
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.checkBox
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.waitForProjectOpen
import com.intellij.ide.starter.driver.engine.BackgroundRun
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import com.intellij.util.applyIf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

@Tag("ui")
class DSPluginSettingUITest {
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
    fun `opening setting and check the ds settings tab behaviour and savings options`() {
        run.driver.withContext {
            ideFrame {
                waitForProjectOpen(1.minutes)
                waitForIndicators(8.minutes)

                openSettingsDialog()
                lateinit var switchedOffOptions: List<UiComponent>
                settingsDialog {
                    openTreeSettingsSection(SETTINGS_NAME)
                    //checking name on top of custom settings
                    val breadcrumbs = x(xQuery { byVisibleText(SETTINGS_NAME) })
                    breadcrumbs.getAllTexts().asString() shouldContain SETTINGS_NAME
                    // Get info about options (list of checkboxes, all should be enabled
                    val options = getOptions()
                    val allOptions = options.size
                    val enabledOptions = options
                        .map { it.checkBox() }
                        .count { it.isSelected() }
                    allOptions shouldBe enabledOptions
                    // switch off 3 randomly, selected should be less than all options
                    switchedOffOptions = options
                        .shuffled()
                        .take(3)
                    switchedOffOptions
                        .forEach { it.click() }
                    val enabledOptionsAfterClick = options
                        .map { it.checkBox() }
                        .count { it.isSelected() }
                    enabledOptionsAfterClick shouldNotBe allOptions
                    //applied and ok custom settings
                    applyButton.click()
                    okButton.click()
                }

                //open again settings and check if the same options are disabled
                openSettingsDialog()
                settingsDialog {
                    openTreeSettingsSection(SETTINGS_NAME)
                    val newOptions = getOptions()
                    val disabledOptions = newOptions
                        .map { it.checkBox() }
                        .filter { !it.isSelected() }
                        .map { it.getParent() }
                        .map { it.getAllTexts().asString() }
                    disabledOptions shouldContainExactlyInAnyOrder switchedOffOptions.map {
                        it.getParent().getAllTexts().asString()
                    }
                }
            }
        }
    }

    private fun Finder.getOptions(): List<UiComponent> {
        val optionsContainer = x(xQuery { byAccessibleName(DS_PLUGIN_CONFIGURABLE_NAME) })
        return optionsContainer.getAllVerticallyOrderedUiText()
            .flatten()
            .map {
                x(xQuery { byVisibleText(it.text) })
            }
    }

}
