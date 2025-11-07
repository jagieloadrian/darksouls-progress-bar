package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSSettingsListener
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URL
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent.Type
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

object TestFailureWindow {
    private var shouldShow = DSPersistentState.getInstance().animateOnFailedBuild
    private var popup: JBPopup? = null
    private var clip: Clip? = null

    init {
        ApplicationManager.getApplication()
            .messageBus
            .connect()
            .subscribe(
                DSSettingsListener.TOPIC,
                object : DSSettingsListener {
                    override fun settingsChanged(newState: DSPersistentState) {
                        shouldShow = newState.animateOnFailedBuild
                    }
                })
    }

    fun show() {
        if (!shouldShow) {
            return
        }
        if (popup != null) return

        val gifUrl = javaClass.getResource(Items.YOU_DIED_GIF)
        val soundUrl = javaClass.getResource(Items.YOU_DIED_SOUND)

        val icon = ImageIcon(gifUrl)

        val panel = JPanel().apply {
            preferredSize = Dimension(icon.iconWidth, icon.iconHeight)
            val label = JLabel(icon).apply {
                accessibleContext.accessibleName = getJavaClassName()
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        TestFailureWindow.hide()
                        super.mouseClicked(e)
                    }
                })
            }
            add(label)
        }

        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .apply {
                setResizable(false)
                setMovable(false)
                setRequestFocus(true)
                setCancelOnClickOutside(true)
                setShowBorder(false)
            }
            .createPopup()

        popup?.addListener(object : JBPopupListener {
            override fun onClosed(event: LightweightWindowEvent) {
                hide()
            }
        })

        popup?.showInFocusCenter()
        playSoundAndClose(soundUrl = soundUrl)
    }

    private fun playSoundAndClose(soundUrl: URL?) {
        try {
            val audio = AudioSystem.getAudioInputStream(soundUrl)
            clip = AudioSystem.getClip()
            clip?.open(audio)
            clip?.start()

            clip?.addLineListener { event ->
                if (event.type == Type.STOP) {
                    clip?.close()
                    SwingUtilities.invokeLater { hide() }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hide() {
        popup?.cancel()
        clip?.let {
            if (it.isRunning) it.stop()
            it.close()
        }
        popup = null
        clip = null
    }

    private fun getJavaClassName():String {
        return this.javaClass.simpleName
    }
}