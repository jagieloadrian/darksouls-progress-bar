package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.intellij.util.messages.Topic

interface DSSettingsListener {
    fun settingsChanged(newState: DSPersistentState)

    companion object {
        val TOPIC = Topic.create("DSPersistentStateChanged", DSSettingsListener::class.java)
    }
}