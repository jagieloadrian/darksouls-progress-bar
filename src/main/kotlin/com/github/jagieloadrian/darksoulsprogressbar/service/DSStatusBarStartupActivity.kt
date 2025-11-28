package com.github.jagieloadrian.darksoulsprogressbar.service

import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.DS_ONE_SOUNDTRACK
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.DS_THREE_SOUNDTRACK
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.DS_TWO_SOUNDTRACK
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.ELDEN_RING_SOUNDTRACK
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.SMALL_BONFIRE_GIF
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.CUSTOM_WIDGET_NAME
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
import javax.swing.Icon
import javax.swing.ImageIcon

class DSSpinnerStatusBarFactory : StatusBarWidgetFactory {
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

class FireSpinnerWidget(private val spinner: FireSpinner = FireSpinner()) : StatusBarWidget.Multiframe, StatusBarWidget.IconPresentation {
    private val soundtracks = listOf(
        DS_ONE_SOUNDTRACK, DS_TWO_SOUNDTRACK,
        DS_THREE_SOUNDTRACK, ELDEN_RING_SOUNDTRACK
    )

    override fun ID(): @NonNls String {
        return CUSTOM_WIDGET_NAME
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }

    override fun copy(): StatusBarWidget {
        return FireSpinnerWidget()
    }

    override fun getIcon(): Icon {
        return spinner.getIcon()
    }

    override fun getTooltipText(): @NlsContexts.Tooltip String {
        return CUSTOM_WIDGET_NAME
    }

    override fun getClickConsumer(): Consumer<MouseEvent> {
        return Consumer {
            try {
                val url = URI(soundtracks.random())
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class FireSpinner {
    private val fireSpinner: ImageIcon = ImageIcon(javaClass.getResource(SMALL_BONFIRE_GIF))

    fun getIcon(): ImageIcon {
        return fireSpinner
    }
}