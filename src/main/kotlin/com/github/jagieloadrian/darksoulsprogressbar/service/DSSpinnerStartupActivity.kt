package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.ui.FireSpinner
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.CUSTOM_WIDGET_NAME
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.Consumer
import org.jetbrains.annotations.NonNls
import java.awt.Desktop
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.ImageIcon

class DSSpinnerStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            statusBar?.let {
                val widget = FireSpinnerWidget()
                it.addWidget(widget)
            }
        }
    }
}

class FireSpinnerWidget : StatusBarWidget {
    private val spinner = FireSpinner()
    override fun ID(): @NonNls String {
        return CUSTOM_WIDGET_NAME
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return CustomSpinnerPresentation(spinner)
    }
}

class CustomSpinnerPresentation(private val spinner: FireSpinner) : StatusBarWidget.IconPresentation {
    override fun getTooltipText(): String = CUSTOM_WIDGET_NAME
    override fun getClickConsumer(): Consumer<MouseEvent> {
        return Consumer {
            try {
                val url = URI("https://www.youtube.com/watch?v=iZ0Yp79Odl8&list=PLxh_0CD1_70Or5zVrJS7G5jWehpTzFNH6")
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun getIcon(): ImageIcon {
       return spinner.getIcon()
    }
}