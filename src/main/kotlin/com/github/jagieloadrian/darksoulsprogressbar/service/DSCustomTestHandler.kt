package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.ui.TestFailureOverlay
import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.VOID_EXECUTION_TOPIC
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

@Service
class DSCustomTestHandler : ExecutionListener {
    override fun processTerminated(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler,
        exitCode: Int,
    ) {
        if (exitCode != 0 && executorId == "Run") {
            ApplicationManager.getApplication().invokeLater {
                TestFailureOverlay.show()
            }
        }
    }
}

class TestFailureStartActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val connection = project.messageBus.connect()
        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, DSCustomTestHandler())
    }

}