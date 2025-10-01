package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.utils.Icons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.IconLoader
import com.intellij.util.ui.JBUI
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicProgressBarUI


class DSProgressBarUI : BasicProgressBarUI() {
    val logger = Logger.getInstance(DSProgressBarUI::class.java)

    private val fireBackground: ImageIcon = ImageIcon(javaClass.getResource("/gif/fire.gif"))
    private val chosenGif: ImageIcon = ImageIcon(javaClass.getResource(Icons.icons.random()))
    var offset = 0
    var offset2 = 0
    var velocity = 1

    companion object {
        fun createUI(c: JComponent): ComponentUI {
            c.setBorder(JBUI.Borders.empty().asUIResource())
            return DSProgressBarUI()
        }
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int {
        return super.getBoxLength(availableLength, otherDimension)
    }

    override fun paintIndeterminate(g2d: Graphics?, c: JComponent?) {
        soutImages()
        if (g2d == null || c == null) return
        val g = g2d as Graphics2D
        val width = c.width
        val height = c.height

        g.drawImage(fireBackground.image, 0, 0, width, height, c)
        val img = chosenGif.image
        chosenGif.imageObserver = c

        val x = (width - img.getWidth(c)) / 2
        val y = (height - img.getHeight(c)) / 2

        g.drawImage(img, x, y, c)
    }

    override fun paintDeterminate(g2d: Graphics?, c: JComponent?) {
        soutImages()
        if (g2d == null || c == null) return
        val g = g2d as Graphics2D
        val width = c.width
        val height = c.height

        g.drawImage(fireBackground.image, 0, 0, width, height, c)
        val amountFull = getAmountFull(null, width, height)
        g.color = progressBar.foreground
        g.fillRect(0, 0, amountFull, height)

        chosenGif.imageObserver = c
        val img = chosenGif.image
        val x = (width - img.getWidth(c)) / 2
        val y = (height - img.getHeight(c)) / 2
        g.drawImage(img, x, y, c)

        if (progressBar.isStringPainted) {
            paintString(g, 0, 0, width, height, amountFull, progressBar.insets)
        }
    }

    fun soutImages() {
        Icons.icons.forEach {
            logger.info("Obrazek: ${javaClass.getResource(it)}")
            logger.info("Obrazek z inną metodą: ${IconLoader.getIcon(it, DSProgressBarUI::class.java)}")
            println("Obrazek: ${javaClass.getResource(it)}")
        }
    }
}