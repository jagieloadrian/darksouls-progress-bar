package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.utils.Icons
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.Timer
import javax.swing.plaf.basic.BasicProgressBarUI

class DSProgressBarUI : BasicProgressBarUI() {

    private val background: ImageIcon = ImageIcon(javaClass.getResource("/gif/toxic_cloud.gif"))
    private val chosenGif: ImageIcon = ImageIcon(javaClass.getResource(Icons.icons.random()))
    private var gifXPosition = 0f
    private val gifSpeed = 5f

    init {
        // Set up a timer to update the GIF position and trigger repaint
        val timer = Timer(16) { // ~60 FPS
            gifXPosition += gifSpeed
        }
        timer.start()
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int {
        return super.getBoxLength(availableLength, otherDimension)
    }

    override fun paintIndeterminate(g2d: Graphics?, c: JComponent?) {
        if (c != null) {
            val gifWidth = chosenGif.iconWidth
            if (gifXPosition > c.width) {
                gifXPosition = -gifWidth.toFloat()
            }
            paintProgressBarWithGif(g2d, c, background, chosenGif, gifXPosition.toInt())
        }
    }

    override fun paintDeterminate(g2d: Graphics?, c: JComponent?) {
        if (c != null) {
            paintProgressBarWithGif(g2d, c, background, chosenGif)
        }
    }

    fun paintProgressBarWithGif(
        g: Graphics?,
        c: JComponent?,
        backgroundIcon: ImageIcon,
        centerIcon: ImageIcon,
        xPosition: Int? = null
    ) {
        if (g == null || c == null) return
        val g2 = g as Graphics2D

        val w = c.width
        val h = c.height

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
}