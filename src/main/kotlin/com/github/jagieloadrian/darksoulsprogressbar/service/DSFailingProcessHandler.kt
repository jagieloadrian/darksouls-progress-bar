package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.ui.TestFailureWindowUI
import com.github.jagieloadrian.darksoulsprogressbar.ui.TestFailureWindowApi
import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

@Service
class DSFailingProcessHandler(
    private val window: TestFailureWindowApi = TestFailureWindowUI
) : ExecutionListener {
    override fun processTerminated(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler,
        exitCode: Int,
    ) {
        if (shouldShowWindow(exitCode, executorId)) {
            ApplicationManager.getApplication().invokeLater {
                window.show()
            }
        }
    }

    fun shouldShowWindow(exitCode: Int, executorId: String): Boolean = exitCode != 0 && executorId == "Run"
}

class TestFailureStartActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val connection = project.messageBus.connect()
        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, DSFailingProcessHandler())
    }
}