package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.CUSTOM_WIDGET_NAME
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.NonNls
import java.awt.Desktop
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.ImageIcon

class DSSpinnerStatusBarFactory: StatusBarWidgetFactory {
    override fun getId(): @NonNls String = CUSTOM_WIDGET_NAME
    override fun getDisplayName(): @NlsContexts.ConfigurableName String = CUSTOM_WIDGET_NAME

    override fun createWidget(
        project: Project,
        scope: CoroutineScope,
    ): StatusBarWidget {
       return FireSpinnerWidget()
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        super.disposeWidget(widget)
        widget.dispose()
    }

    override fun isEnabledByDefault(): Boolean = true

    override fun isAvailable(project: Project): Boolean = true
}

class FireSpinnerWidget : StatusBarWidget, StatusBarWidget.Multiframe {
    private val spinner = FireSpinner()
    override fun ID(): @NonNls String {
        return CUSTOM_WIDGET_NAME
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return FireSpinnerPresentation(spinner)
    }

    override fun copy(): StatusBarWidget {
        return FireSpinnerWidget()
    }
}

class FireSpinnerPresentation(private val spinner: FireSpinner) : StatusBarWidget.IconPresentation {
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

class FireSpinner {
    private val fireSpinner: ImageIcon = ImageIcon(javaClass.getResource("/gif/bonfire_darksouls_small.gif"))

    fun getIcon(): ImageIcon {
        return fireSpinner
    }
}