package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSSettingsListener
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items.BACKGROUND_PROGRESS_BAR_GIF
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.scale.JBUIScale
import java.awt.BasicStroke
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.RoundRectangle2D
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.Timer
import javax.swing.plaf.basic.BasicProgressBarUI

class DSProgressBarUI : BasicProgressBarUI() {

    private val background: ImageIcon = ImageIcon(javaClass.getResource(BACKGROUND_PROGRESS_BAR_GIF))
    private var iconsToUse = DSPersistentState.getInstance().iconPaths
    private val chosenGif: ImageIcon = ImageIcon(javaClass.getResource(iconsToUse.random()))
    private var gifXPosition = 0f
    private val gifSpeed = 2f
    private var shouldBackward = false

    init {
        // Set up a timer to update the GIF position and trigger repaint
        val timer = Timer(32) { // ~30 FPS
            if (shouldBackward) {
                gifXPosition -= gifSpeed
            } else {
                gifXPosition += gifSpeed
            }
        }
        timer.start()

        ApplicationManager.getApplication()
            .messageBus
            .connect()
            .subscribe(
                DSSettingsListener.TOPIC,
                object : DSSettingsListener {
                    override fun settingsChanged(newState: DSPersistentState) {
                        iconsToUse = newState.iconPaths
                    }
                })
    }

    override fun paintIndeterminate(g2d: Graphics?, c: JComponent?) {
        if (c != null) {
            c.accessibleContext?.accessibleName = this.javaClass.simpleName
            changeShouldBackward(gifXPosition.toInt(), c.width, chosenGif.iconWidth)
            paintProgressBar(g2d, c, background, chosenGif, gifXPosition.toInt())
        }
    }

    private fun changeShouldBackward(xPosition: Int, componentWidth: Int, iconWidth: Int) {
        if (xPosition > componentWidth) {
            shouldBackward = true
        }
        if (xPosition + iconWidth < 0) {
            shouldBackward = false
        }
    }

    override fun getPreferredSize(c: JComponent?): Dimension {
        val d:Dimension =  super.getPreferredSize(c)
        d.height = 20
        return d
    }

    override fun paintDeterminate(g2d: Graphics?, c: JComponent?) {
        if (c != null) {
            c.accessibleContext?.accessibleName = this.javaClass.simpleName
            paintProgressBar(g2d, c, background, chosenGif)
        }
    }

    private fun paintProgressBar(
        g: Graphics?,
        c: JComponent?,
        backgroundIcon: ImageIcon,
        centerIcon: ImageIcon,
        xPosition: Int? = null,
    ) {
        if (g == null || c == null) return
        val g2 = g as Graphics2D
        val rectangle2D = getRoundRectangle(c.width, c.height)
        paintProgressBarWithGif(g2, rectangle2D, backgroundIcon, c, centerIcon, xPosition)
        drawBorder(rectangle2D, g2)

    }

    private fun paintProgressBarWithGif(
        g2: Graphics2D,
        rectangle2D: RoundRectangle2D,
        backgroundIcon: ImageIcon,
        c: JComponent,
        centerIcon: ImageIcon,
        xPosition: Int?,
    ) {
        val w = c.width
        val h = c.height
        g2.fill(rectangle2D)
        g2.drawImage(backgroundIcon.image, 0, 0, w, h, c)

        val origW = centerIcon.iconWidth
        val origH = centerIcon.iconHeight

        var targetH = h
        var targetW = (origW * targetH / origH.toDouble()).toInt()

        if (targetW > w) {
            val scale = w.toDouble() / targetW
            targetW = w
            targetH = (targetH * scale).toInt()
        }

        val x = xPosition ?: ((w - targetW) / 2)
        val y = (h - targetH) / 2

        g2.drawImage(centerIcon.image, x, y, targetW, targetH, c)
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
}