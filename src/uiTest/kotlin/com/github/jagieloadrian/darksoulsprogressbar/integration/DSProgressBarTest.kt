package com.github.jagieloadrian.darksoulsprogressbar.integration

import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.NoProject
import com.intellij.ide.starter.runner.Starter
import com.intellij.util.applyIf
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.Paths

@Tag("ui")
class DSProgressBarTest {

    @Test
    fun `simple context test wo prodżekto`() {
        Starter.newContext(
            "Test Context",
            TestCase(IdeProductProvider.IC, projectInfo = NoProject)
                .withVersion("2024.1")
        ).applyIf(true) {
            val pluginPath = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromPath(Paths.get(pluginPath))
            this
        }.runIdeWithDriver().useDriverAndCloseIde {  }
    }
}
