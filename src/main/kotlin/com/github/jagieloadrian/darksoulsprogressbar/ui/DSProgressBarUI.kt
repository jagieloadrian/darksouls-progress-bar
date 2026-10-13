package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSSettingsListener
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.BACKGROUND_PROGRESS_BAR_GIF
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.util.ProgressBarUtil
import com.intellij.ui.scale.JBUIScale
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.UIManager
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.abs
import kotlin.math.sin

class DSProgressBarUI : BasicProgressBarUI() {

    private val background: ImageIcon = ImageIcon(javaClass.getResource(BACKGROUND_PROGRESS_BAR_GIF))
    private var iconsToUse = DSPersistentState.getInstance().iconPaths
    private var chosenGif: ImageIcon = pickIcon()
    private val cycleDurationMs = 5000
    private val startTime = System.currentTimeMillis()

    private val connection = ApplicationManager.getApplication().messageBus.connect().apply {
        subscribe(
            DSSettingsListener.TOPIC,
            object : DSSettingsListener {
                override fun settingsChanged(newState: DSPersistentState) {
                    iconsToUse = newState.iconPaths
                    chosenGif = pickIcon()
                }
            })
    }

    override fun uninstallUI(c: JComponent?) {
        connection.disconnect()
        super.uninstallUI(c)
    }

    override fun getPreferredSize(c: JComponent?): Dimension {
        val d: Dimension = super.getPreferredSize(c)
        d.height = 20
        return d
    }

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
        if (g == null || c == null) return
        c.accessibleContext?.accessibleName = this.javaClass.simpleName
        paintBar(g, c, fraction = 1.0, iconX = sweepX(c.width, c.height))
        if (c.isShowing) c.repaint(33)
    }

    override fun paintDeterminate(g: Graphics?, c: JComponent?) {
        if (g == null || c == null) return
        c.accessibleContext?.accessibleName = this.javaClass.simpleName
        paintBar(g, c, fraction = progressBar.percentComplete.coerceIn(0.0, 1.0), iconX = null)
    }

    private fun paintBar(g: Graphics, c: JComponent, fraction: Double, iconX: Int?) {
        val w = c.width
        val h = c.height
        if (w <= 0 || h <= 0) return
        val shape = getRoundRectangle(w, h)
        val flip = progressBar.getClientProperty("ProgressBar.flip") == true
        val fillW = (w * fraction).toInt()
        val fillX = if (flip) w - fillW else 0

        val g2 = g.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.clip(shape)

            g2.color = UIManager.getColor("ProgressBar.trackColor") ?: progressBar.background
            g2.fill(shape)

            val fill = g2.create(fillX, 0, fillW, h) as Graphics2D
            try {
                fill.translate(-fillX, 0)
                tileBackground(fill, c, w, h)
                statusTint()?.let {
                    fill.color = it
                    fill.fillRect(0, 0, w, h)
                }
            } finally {
                fill.dispose()
            }

            val icon = chosenGif
            val iconH = h
            val iconW = minOf(w, (icon.iconWidth * iconH / icon.iconHeight.toDouble()).toInt())
            val x = iconX ?: if (flip) fillX else (fillX + fillW - iconW)
            if (fillW > 0 || iconX != null) {
                g2.drawImage(icon.image, x.coerceIn(0, w - iconW), 0, iconW, iconH, c)
            }
        } finally {
            g2.dispose()
        }
        drawBorder(shape, g as Graphics2D)
    }

    private fun tileBackground(g2: Graphics2D, c: JComponent, w: Int, h: Int) {
        val tileW = maxOf(1, (background.iconWidth * h / background.iconHeight.toDouble()).toInt())
        var x = 0
        while (x < w) {
            g2.drawImage(background.image, x, 0, tileW, h, c)
            x += tileW
        }
    }

    private fun statusTint(): Color? = when (progressBar.getClientProperty(ProgressBarUtil.STATUS_KEY)) {
        ProgressBarUtil.FAILED_VALUE -> Color(0xE5, 0x45, 0x45, 130)
        ProgressBarUtil.WARNING_VALUE -> Color(0xF2, 0xC5, 0x5C, 110)
        ProgressBarUtil.PASSED_VALUE -> Color(0x5F, 0xB8, 0x65, 110)
        else -> null
    }

    private fun drawBorder(rectangle2D: RoundRectangle2D, graphics2D: Graphics2D) {
        graphics2D.color = progressBar.foreground
        graphics2D.stroke = BasicStroke(1f)
        graphics2D.draw(rectangle2D)
    }

    private fun getRoundRectangle(width: Int, height: Int): RoundRectangle2D {
        val arcLength = JBUIScale.scale(9f)
        val offset = JBUIScale.scale(2f)
        return RoundRectangle2D.Float(
            JBUIScale.scale(1f),
            JBUIScale.scale(1f),
            width - offset,
            height - offset,
            arcLength,
            arcLength,
        )
    }

    private fun pickIcon(): ImageIcon {
        val url = iconsToUse.randomOrNull()?.let { javaClass.getResource(it) }
        return if (url != null) ImageIcon(url) else ImageIcon(BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))
    }

    private fun sweepX(availableWidth: Int, height: Int): Int {
        val icon = chosenGif
        val iconW = (icon.iconWidth * height / icon.iconHeight.toDouble()).toInt()
        val elapsed = (System.currentTimeMillis() - startTime) % cycleDurationMs
        val progress = elapsed.toDouble() / cycleDurationMs
        return ((availableWidth - iconW) * abs(sin(progress * Math.PI))).toInt()
    }
}
