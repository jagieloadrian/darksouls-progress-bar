package com.github.jagieloadrian.darksoulsprogressbar.ui

import com.github.jagieloadrian.darksoulsprogressbar.settings.DSPersistentState
import com.github.jagieloadrian.darksoulsprogressbar.settings.DSSettingsListener
import com.github.jagieloadrian.darksoulsprogressbar.utils.Items
import com.github.jagieloadrian.darksoulsprogressbar.utils.Names
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
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
import javax.swing.Timer

interface TestFailureWindowApi{
    fun show()
}

object TestFailureWindowUI : TestFailureWindowApi{
    private var shouldShow: Boolean? = null
    private var popup: JBPopup? = null
    private var clip: Clip? = null

    init {
        ApplicationManager.getApplication()
            .messageBus
            .connect(ApplicationManager.getApplication())
            .subscribe(
                DSSettingsListener.TOPIC,
                object : DSSettingsListener {
                    override fun settingsChanged(newState: DSPersistentState) {
                        shouldShow = newState.animateOnFailedBuild
                    }
                })
    }

    override fun show() {
        if (shouldShow == null) {
            shouldShow = DSPersistentState.getInstance().animateOnFailedBuild
        }
        if (shouldShow != true) return
        if (popup != null) return

        val death = Items.DEATHS.random()
        val gifUrl = javaClass.getResource(death.gif) ?: return
        val soundUrl = javaClass.getResource(death.sound)

        val icon = ImageIcon(gifUrl)

        val panel = JPanel().apply {
            preferredSize = Dimension(icon.iconWidth, icon.iconHeight)
            val label = JLabel(icon).apply {
                accessibleContext.accessibleName = Names.TEST_FAILURE_WINDOW_NAME
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        TestFailureWindowUI.hide()
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
            requireNotNull(soundUrl) { "Missing death sound" }
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
            thisLogger().warn("Cannot play death sound", e)
            // no sound to wait for: close on a timer so the popup doesn't linger
            Timer(FALLBACK_CLOSE_MS) { hide() }.apply { isRepeats = false }.start()
        }
    }

    private const val FALLBACK_CLOSE_MS = 3000

    private fun hide() {
        popup?.cancel()
        clip?.let {
            if (it.isRunning) it.stop()
            it.close()
        }
        popup = null
        clip = null
    }
}