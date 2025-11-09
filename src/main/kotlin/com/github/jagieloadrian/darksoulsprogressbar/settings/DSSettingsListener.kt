package com.github.jagieloadrian.darksoulsprogressbar.settings

import com.github.jagieloadrian.darksoulsprogressbar.utils.Names.DS_PERSISTENT_TOPIC_NAME
import com.intellij.util.messages.Topic

interface DSSettingsListener {
    fun settingsChanged(newState: DSPersistentState)

    companion object {
        val TOPIC = Topic.create(DS_PERSISTENT_TOPIC_NAME, DSSettingsListener::class.java)
    }
}